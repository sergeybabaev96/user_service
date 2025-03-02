package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int ACCOUNT_DEACTIVATION_PERIOD_DAYS = 90;
    private final UserRepository userRepository;
    private final List<UserFilter> users;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;

    public UserReadDto deactivate(Long userId) {
        User user = getById(userId);
        removeEvents(userId);
        removeGoals(userId);
        removeMenteeAndGoals(userId);
        user.setActive(false);
        return userMapper.toDto(userRepository.save(user));
    }

    public void removeMenteeAndGoals(Long userId) {
        mentorshipService.removeMenteeGoals(userId);
        mentorshipService.removeMenteeFromUser(userId);
    }

    public void deleteInactiveUsers() {
        userRepository.findAll().stream()
                .filter(user -> !user.isActive() && user.getUpdatedAt().plusDays(ACCOUNT_DEACTIVATION_PERIOD_DAYS).isBefore(LocalDateTime.now()))
                .forEach(userRepository::delete);
    }

    private void removeEvents(Long userId) {
        isUserExists(userId);
        User user = getById(userId);
        if (user.getOwnedEvents() != null && !user.getOwnedEvents().isEmpty()) {
            user.getOwnedEvents().forEach(event ->
                    event.setStatus(EventStatus.CANCELED));
            eventRepository.saveAll(user.getOwnedEvents());
        }
    }

    private void removeGoals(Long userId) {
        isUserExists(userId);
        User user = getById(userId);
        if (user.getGoals() != null && !user.getGoals().isEmpty()) {
            user.getGoals().forEach(goal -> {
                goal.getUsers().remove(user);
                if (goal.getUsers().isEmpty()) {
                    goalRepository.delete(goal);
                }
            });
        }
    }

    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователя с id " + userId + " не существует");
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllByIds(@NotNull List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Невозможно получить пользователя"));
    }

    public UserReadDto getUser(long userId) {
        return userMapper.toDto(getById(userId));
    }

    public List<UserReadDto> getUsersByIds(List<Long> ids) {
        return getAllByIds(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserReadDto> getPremiumUsers(UserFilterDto filter) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        users.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .forEach(userFilter -> userFilter.apply(premiumUsers, filter));

        return premiumUsers.map(userMapper::toDto).toList();
    }
}
