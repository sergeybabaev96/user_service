package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
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
    @Autowired
    private GoalService goalService;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MentorshipService mentorshipService;

    private static final String LOG_MESSAGE_DEACTIVATING_STARTS = "Deactivating user with id={}";
    private static final String LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS = "Quitting participation in goals for userId = {} goal={}";
    private static final String LOG_MESSAGE_DELETE_EVENTS = "Deleting events for userId={} event={}";
    private static final String ERROR_USER_IS_NOT_PRESENTED_IN_DB = "User is not presented in DB";
    private static final String LOG_MESSAGE_DELETE_GOAL = "Deleting goals fot  userId={} goal={} ";

    public void deactivateUser(Long userId) {
        log.info(LOG_MESSAGE_DEACTIVATING_STARTS, userId);
        User userToDeactivate = getUserFromDataBase(userId);

        quitGoals(userToDeactivate.getId());
        quitEvents(userToDeactivate.getId());
        quitMentorship(userToDeactivate);

        userToDeactivate.setActive(false);
        userRepository.save(userToDeactivate);
    }

    private void quitGoals(Long userId) {
        List<Goal> allUserGoals = goalRepository.findGoalsListByUserId(userId);
        List<Goal> goalsToDelete = findGoalsToDelete(userId);

        for (Goal goal : goalsToDelete) {
            goalService.deleteGoal(goal.getId());
            log.debug(LOG_MESSAGE_DELETE_GOAL, userId, goal.getDescription());
        }

        for (Goal goal : allUserGoals) {
            List<User> usersOfGoalWithoutDeactivatedUser = goal.getUsers().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .toList();
            goal.setUsers(usersOfGoalWithoutDeactivatedUser);
            goalService.updateGoal(userId, goal);
            log.debug(LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS, userId, goal.getDescription());
        }
    }

    private void quitEvents(Long userId) {
        List<Event> eventsToDelete = eventRepository.findAllByUserId(userId);
        for (Event event : eventsToDelete) {
            eventService.deleteEvent(event.getId());
            log.debug(LOG_MESSAGE_DELETE_EVENTS, userId, event.getDescription());
        }
    }

    private void quitMentorship(User userToDeactivate) {
        mentorshipService.stopMentorship(userToDeactivate.getId());
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

    private List<Goal> findGoalsToDelete(Long userId) {

        List<Goal> userGoals = goalRepository.findGoalsListByUserId(userId);
        List<Goal> goalsToDelete = new ArrayList<>();
        for (Goal goal : userGoals) {
            List<User> users = goalRepository.findUsersByGoalIdHql(goal.getId());
            if (users.size() == 1) {
                goalsToDelete.add(goal);
            }
        }
        return goalsToDelete;
    }
}
