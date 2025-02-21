package school.faang.user_service.service.user;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;

    private List<User> userListSizeOne;
    private List<Goal> userGoals;
    private List<Goal> allUserGoals;

    private List<Goal> allUserGoalsEmpty;
    private List<Event> eventsEmpty;

    @BeforeEach
    public void setUp() {
        userListSizeOne = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        userListSizeOne.add(user);

        userGoals = new ArrayList<>();
        Goal goal = new Goal();
        goal.setId(2L);
        goal.setUsers(List.of(user));
        userGoals.add(goal);

        allUserGoals = new ArrayList<>();
        Goal goalA = new Goal();
        goalA.setId(2L);
        goalA.setUsers(List.of(user));
        allUserGoals.add(goalA);

        allUserGoalsEmpty = new ArrayList<>();
        eventsEmpty = new ArrayList<>();
    }

    @Test
    public void testDeactivateUserGoalIsDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoals);
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(userGoals);
        when(goalRepository.findUsersByGoalIdHql(Mockito.anyLong())).thenReturn(userListSizeOne);

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).deleteGoal(Mockito.anyLong());
    }

    @Test
    public void testDeactivateUserDeleteGoalIsNotInvoked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(eventRepository.findAllByUserId(Mockito.anyLong())).thenReturn(eventsEmpty);

        userService.deactivateUser(1L);

        Mockito.verify(goalService, never()).deleteGoal(Mockito.anyLong());
    }

    @Test
    public void testDeactivateUserGoalIsUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoals);
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(userGoals);
        when(eventRepository.findAllByUserId(Mockito.anyLong())).thenReturn(eventsEmpty);

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).updateGoal(Mockito.anyLong(), Mockito.any(Goal.class));
    }

    @Test
    public void testDeactivateUserEventsAreDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(eventRepository.findAllByUserId(Mockito.anyLong())).thenReturn(eventsEmpty);
        when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(new Event()));

        userService.deactivateUser(1L);

        Mockito.verify(eventService, Mockito.times(1)).deleteEvent(Mockito.anyLong());
    }

    @Test
    public void testDeactivateUserStopMentorship() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(goalRepository.findGoalsListByUserId(1L)).thenReturn(allUserGoalsEmpty);
        when(eventRepository.findAllByUserId(Mockito.anyLong())).thenReturn(eventsEmpty);
        when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(new Event()));

        userService.deactivateUser(1L);

        Mockito.verify(mentorshipService, Mockito.times(1)).stopMentorship(userListSizeOne.get(0).getId());
    }
}
