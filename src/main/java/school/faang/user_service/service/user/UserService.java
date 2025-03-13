package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private GoalService goalService;
    private EventService eventService;
    private UserRepository userRepository;
    private MentorshipService mentorshipService;

    private static final String LOG_MESSAGE_DEACTIVATING_STARTS = "Deactivating user with id={}";
    private static final String LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS = "Quitting participation in goals for userId = {} goal={}";
    private static final String LOG_MESSAGE_DELETE_EVENTS = "Deleting events for userId={} event={}";
    private static final String ERROR_USER_IS_NOT_PRESENTED_IN_DB = "User is not presented in DB";
    private static final String LOG_MESSAGE_DELETE_GOAL = "Deleting goals fot  userId={} goal={} ";

    public void deactivateUser(Long userId) {
        log.info(LOG_MESSAGE_DEACTIVATING_STARTS, userId);
        User userToDeactivate = getUserFromDataBase(userId);

        quitGoals(userToDeactivate);
        stopAndDeleteEvent(userToDeactivate);
        quitMentorship(userToDeactivate);

        userToDeactivate.setActive(false);
        userRepository.save(userToDeactivate);
    }

    public User getUser(Long userId) {
        return getUserFromDataBase(userId);
    }

    /**
     * Method does two tasks:
     * 1. Deletes goals where user is the only participant
     * 2. Removes user from the participant's list of other goals
     * @param user - user who is in the process of deactivation
     */
    private void quitGoals(User user) {
        removeGoalsWhereUserIsTheOnlyAssigneeOrHasNoAssignee(user);
        removeUserFromGoalParticipants(user);
    }

    private void removeGoalsWhereUserIsTheOnlyAssigneeOrHasNoAssignee(User user) {
        List<Goal> goalsToDelete = findGoalsToDelete(user.getId(), user.getGoals());

        for (Goal goal : goalsToDelete) {
            goalService.deleteGoal(goal);
            log.debug(LOG_MESSAGE_DELETE_GOAL, user.getId(), goal.getDescription());
        }
    }

    /**
     * Removes user from the participant's list of goals the user is participating in
     * @param user - user who is in the process of deactivation
     */
    private void removeUserFromGoalParticipants(User user) {

        for (Goal goal : user.getGoals()) {
                goal.getUsers().remove(user);
                goalService.updateGoal(user.getId(), goal);
                log.debug(LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS, user.getId(), goal.getDescription());
        }
    }

    private void stopAndDeleteEvent(User user) {
        List<Event> eventsToDelete = user.getOwnedEvents();
        for (Event event : eventsToDelete) {
            List<User> eventAttendees = event.getAttendees();
            event.getAttendees().removeAll(eventAttendees);
            eventService.deleteEvent(event);
            log.debug(LOG_MESSAGE_DELETE_EVENTS, user.getId(), event.getDescription());
        }
        user.getOwnedEvents().removeAll(eventsToDelete);
        eventsToDelete = user.getParticipatedEvents();
        user.getParticipatedEvents().removeAll(eventsToDelete);
    }

    private void quitMentorship(User user) {
        mentorshipService.stopMentorship(user);
    }

    private User getUserFromDataBase(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            log.warn(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
            throw new DataValidationException(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
        }
    }

    private List<Goal> findGoalsToDelete(Long userId, List<Goal> userGoals) {
        List<Goal> goalsToDelete = new ArrayList<>();
        for (Goal goal : userGoals) {
            List<User> users = goal.getUsers();
            if (((users.isEmpty())) ||
                    ((users.size() == 1) && (users.get(0).getId().equals(userId)))) {
                goalsToDelete.add(goal);
            }
        }
        return goalsToDelete;
    }
}
