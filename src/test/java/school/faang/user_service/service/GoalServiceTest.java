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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;


    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    void testDeleteUserFromGoals() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        Goal goal1 = Goal.builder().users(List.of(user)).status(GoalStatus.ACTIVE).build();

        Goal goal2 = Goal.builder().users(List.of(user, new User()))
                .status(GoalStatus.ACTIVE).build();
        List<Goal> goals = List.of(goal1, goal2);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        goalService.deleteUserFromGoals(userId);

        verify(goalRepository,times(1)).delete(goal1);
        verify(goalRepository,times(1)).saveAll(argThat(list ->
                StreamSupport.stream(list.spliterator(), false)
                        .noneMatch(goal -> goal.getUsers().contains(user))
        ));
    }

}