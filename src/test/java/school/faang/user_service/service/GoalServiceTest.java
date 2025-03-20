package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    void testDeleteUserFromGoals() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Goal goal1 = new Goal();
        goal1.setUsers(List.of(user));
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setUsers(List.of(user, new User()));
        goal2.setStatus(GoalStatus.ACTIVE);

        List<Goal> goals = List.of(goal1, goal2);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        goalService.deleteUserFromGoals(userId);


        verify(goalRepository).delete(goal1);
        verify(goalRepository).saveAll(argThat(list ->
                StreamSupport.stream(list.spliterator(), false)
                        .noneMatch(goal -> goal.getUsers().contains(user))
        ));
    }

}