package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void testStopMentorship() {
        User mentor = new User();
        List<User> mentors = new ArrayList<>(List.of(mentor));

        User mentee1 = new User();
        mentee1.setUsername("Mentee 1");
        mentee1.setMentors(mentors);
        User mentee2 = new User();
        mentee2.setUsername("Mentee 2");
        mentee2.setMentors(mentors);
        mentee2.setGoals(new ArrayList<>());
        User mentee3 = new User();
        mentee3.setUsername("Mentee 3");
        mentee3.setMentors(mentors);
        mentee3.setGoals(new ArrayList<>());

        List<User> mentees = new ArrayList<>(List.of(mentee1, mentee2, mentee3));

        Goal goal = new Goal();
        goal.setMentor(mentor);
        goal.setUsers(mentees);
        List<Goal> goals = new ArrayList<>(List.of(goal));
        mentee1.setGoals(goals);

        mentor.setMentees(mentees);

        mentorshipService.stopMentorship(mentor);

        verify(userRepository, times(1)).save(mentor);
        verify(goalRepository, times(1)).save(goal);
        Assertions.assertEquals(0, mentor.getMentees().size());
    }

}
