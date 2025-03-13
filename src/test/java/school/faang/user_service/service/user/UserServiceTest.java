package school.faang.user_service.service.user;

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
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;

    private User userWithNoGoals;
    private List<User> userListSizeOne;
    private Goal goal;
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


        allUserGoals = new ArrayList<>();
        Goal goalA = new Goal();
        goalA.setId(3L);
        goalA.setUsers(List.of(user));
        allUserGoals.add(goalA);

        allUserGoalsEmpty = new ArrayList<>();
        eventsEmpty = new ArrayList<>();
        userWithNoGoals = new User();
        userWithNoGoals.setGoals(allUserGoalsEmpty);
        userWithNoGoals.setOwnedEvents(eventsEmpty);
        userWithNoGoals.setParticipatedEvents(eventsEmpty);

        userGoals = new ArrayList<>();
        goal = new Goal();
        goal.setId(2L);
        goal.setUsers(new ArrayList<>(List.of(user)));
        userGoals.add(goal);
        userListSizeOne.get(0).setGoals(userGoals);
        userListSizeOne.get(0).setOwnedEvents(eventsEmpty);
        userListSizeOne.get(0).setParticipatedEvents(eventsEmpty);
    }

    @Test
    public void testDeactivateUserGoalIsDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).deleteGoal(goal);
    }

    @Test
    public void testDeactivateUserDeleteGoalIsNotInvoked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNoGoals));
        userService.deactivateUser(1L);

        Mockito.verify(goalService, never()).deleteGoal(Mockito.any());
    }

    @Test
    public void testDeactivateUserGoalIsUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).updateGoal(Mockito.anyLong(), Mockito.any(Goal.class));
    }

    @Test
    public void testDeactivateUserEventsAreDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        userListSizeOne.get(0).setOwnedEvents(new ArrayList<>(List.of(new Event())));
        userListSizeOne.get(0).getOwnedEvents().get(0).setAttendees(new ArrayList<>(List.of(new User())));
        userService.deactivateUser(1L);

        Mockito.verify(eventService, Mockito.times(1)).deleteEvent(Mockito.any());
    }

    @Test
    public void testDeactivateUserStopMentorship() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userListSizeOne.get(0)));
        userService.deactivateUser(1L);

        Mockito.verify(mentorshipService, Mockito.times(1)).stopMentorship(userListSizeOne.get(0));
    }
}
