package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.s3.S3Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.utils.user.UserErrorMessage.USERS_NOT_FOUND;
import static school.faang.user_service.utils.user.UserErrorMessage.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserService {
    private final MentorshipService mentorshipService;

    private final UserContext userContext;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final S3Service s3Service;

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
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

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with id = " + userId));
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

    @Transactional
    public String uploadAvatar(MultipartFile file, String size) {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        Pair<UserProfilePic, String> uploadResult = s3Service.uploadAvatar(file, size);

        if (currentUser.getUserProfilePic() != null) {
            String largeImageKey = currentUser.getUserProfilePic().getFileId();
            String smallImageKey = currentUser.getUserProfilePic().getSmallFileId();
            s3Service.deleteAvatar(largeImageKey);
            s3Service.deleteAvatar(smallImageKey);
        }

        currentUser.setUserProfilePic(uploadResult.getFirst());

        userRepository.save(currentUser);

        return uploadResult.getSecond();
    }

    @Transactional(readOnly = true)
    public String downloadAvatar(String size) {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        String imageKey = currentUser.getUserProfilePic().getFileId();
        if (size.equalsIgnoreCase("large")) {
            imageKey = currentUser.getUserProfilePic().getSmallFileId();
        }

        return s3Service.downloadAvatar(imageKey);
    }

    @Transactional
    public void deleteAvatar() {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        String largeImageKey = currentUser.getUserProfilePic().getFileId();
        String smallImageKey = currentUser.getUserProfilePic().getSmallFileId();

        s3Service.deleteAvatar(largeImageKey);
        s3Service.deleteAvatar(smallImageKey);

        currentUser.setUserProfilePic(null);
        userRepository.save(currentUser);
    }

    private void deactivateUserDependencies(Long userId) {
        removeUserFromGoals(userId);
        removeUserEvents(userId);
    }

    private void removeUserFromGoals(Long userId) {
        List<Goal> userGoals = goalRepository.findGoalsByUserId(userId).toList();

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
