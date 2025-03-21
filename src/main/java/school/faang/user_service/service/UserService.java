package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("User with id " + id + " not found"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(userMapper::toDto)
                    .toList();
        }
    }

    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User with id " + id + " not found")
        );

        if (user.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
            user.setActive(true);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User with id " + id + " not found")
        );

        deleteGoalsAndEvents(user);

        user.setActive(false);

        user.getMentees().forEach(mentee -> mentorshipService.deleteMentorship(user.getId(), mentee.getId()));

        return userMapper.toDto(userRepository.save(user));
    }

    private void deleteGoalsAndEvents(User user) {
        user.getGoals().forEach(goal -> {
            user.getGoals().remove(goal);

            goal.getUsers().remove(user);
            goalRepository.save(goal);
            if (goal.getUsers().isEmpty()) {
                goalRepository.deleteById(goal.getId());
            }

            userRepository.save(user);
        });

        user.getOwnedEvents().forEach(event -> {
            user.getOwnedEvents().remove(event);
            userRepository.save(user);

            eventRepository.deleteById(event.getId());
        });
    }
}
