package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @InjectMocks private GoalService goalService;

    @Test
    void testDeleteUserFromGoals() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Goal goal1 = new Goal();
        goal1.setUsers(List.of(user));

        Goal goal2 = new Goal();
        goal2.setUsers(List.of(user, new User()));

        List<Goal> goals = List.of(goal1, goal2);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        goalService.deleteUserFromGoals(userId);

        verify(goalRepository).delete(goal1); // Удаление пустой цели
        verify(goalRepository).saveAll(argThat(list ->
                list.stream().noneMatch(goal -> goal.getUsers().contains(user))
        ));
    }

    @Test
    void testSetNullInGoalsToMentor() {

        Long mentorId = 1L;
        Goal goal1 = new Goal();
        goal1.setMentor(new User());

        Goal goal2 = new Goal();
        goal2.setMentor(new User());

        List<Goal> goals = List.of(goal1, goal2);
        when(goalRepository.findAllByMentorId(mentorId)).thenReturn(goals);

        goalService.setNullInGoalsToMentor(mentorId);

        verify(goalRepository).saveAll(argThat(list ->
                list.stream().allMatch(goal -> goal.getMentor() == null)
        ));
    }
}