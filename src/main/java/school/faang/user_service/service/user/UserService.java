package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.service.user.UserErrorMessage.USERS_NOT_FOUND;
import static school.faang.user_service.service.user.UserErrorMessage.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserService {
    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final UserAvatarService userAvatarService;
    private final UserContext userContext;

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public long getCurrentUserId() {
        return userContext.getUserId();
    }

    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, id)));
    }

    public List<User> getUsersByIds(List<User> users) {
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();
        users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException(USERS_NOT_FOUND);
        }
        return users;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with id = " + userId));
    }

    @Transactional
    public User registerUser(String username, String email, String password, Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + countryId));

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .country(country)
                .active(true)
                .experience(0)
                .build();

        userAvatarService.generateAvatarForNewUser(newUser, AvatarType.JPEG);

        return userRepository.save(newUser);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, userId)));

        deactivateUserDependencies(userId);

        user.setActive(false);
        userRepository.save(user);

        mentorshipService.stopUserMentorship(userId);
    }

    private void deactivateUserDependencies(Long userId) {
        removeUserFromGoals(userId);
        removeUserEvents(userId);
    }

    private void removeUserFromGoals(Long userId) {
        List<Goal> userGoals =  goalRepository.findGoalsByUserId(userId).toList();

        List<Goal> goalsToDelete = userGoals.stream()
                .filter(goal -> goal.getUsers().size() == 1)
                .toList();


        List<Goal> goalsToUpdate = userGoals.stream()
                .filter(goal -> goal.getUsers().size() > 1)
                .peek(goal -> goal.getUsers().removeIf(user -> Objects.equals(user.getId(), userId)))
                .toList();

        goalRepository.deleteAll(goalsToDelete);
        goalRepository.saveAll(goalsToUpdate);
    }

    private void removeUserEvents(Long userId) {
        List<Event> eventsOwnedToCancel = eventRepository.findAllByUserId(userId).stream()
                .filter(event -> Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        List<Event> eventsParticipatedToUpdate = eventRepository.findParticipatedEventsByUserId(userId).stream()
                .filter(event -> !Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.getAttendees().removeIf(attendee ->
                        Objects.equals(attendee.getId(), userId)))
                .toList();

        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(eventsOwnedToCancel);
        allEvents.addAll(eventsParticipatedToUpdate);

        eventRepository.saveAll(allEvents);
    }
}
