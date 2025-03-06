package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.CreateGoalRequest;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.dto.goal.UpdateGoalRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.exception.UserGoalLimitExceededException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.GoalFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    private static final int USER_GOAL_MAX_COUNT = 3;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @InjectMocks
    private GoalService goalService;

    @Test
    void createGoal_Success() {
        long userId = 10L;
        CreateGoalRequest createGoalRequest = CreateGoalRequest.builder()
                .title("Новая цель")
                .description("Описание")
                .skillsToAchieveIds(List.of(1L, 2L))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));

        Goal goal = new Goal();
        goal.setId(101L);
        goal.setTitle("Новая цель");
        goal.setSkillsToAchieve(List.of(skill1, skill2));

        when(goalRepository.create(eq(createGoalRequest.title()), eq(createGoalRequest.description()), any())).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        GoalResponse result = goalService.createGoal(userId, createGoalRequest);

        assertNotNull(result);
        assertEquals(101L, result.id());
        assertEquals("Новая цель", result.title());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_ShouldThrowUserGoalLimitExceededExceptionWhenGoalLimitExceeded() {
        long userId = 10L;
        CreateGoalRequest createGoalRequest = CreateGoalRequest.builder()
                .title("Новая цель")
                .description("Описание")
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(USER_GOAL_MAX_COUNT);

        assertThrows(UserGoalLimitExceededException.class, () -> goalService.createGoal(userId, createGoalRequest));
    }

    @Test
    void createGoal_ShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, CreateGoalRequest.builder().title("Цель").build()));
    }

    @Test
    void createGoal_ShouldThrowSkillNotFoundExceptionWhenSkillNotFound() {
        long userId = 10L;
        CreateGoalRequest createGoalRequest = CreateGoalRequest.builder()
                .title("Новая цель")
                .description("Описание")
                .skillsToAchieveIds(List.of(999L))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(skillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> goalService.createGoal(userId, createGoalRequest));
    }


    @Test
    void updateGoal_ShouldThrowEntityNotFoundExceptionWhenGoalNotFound() {
        UpdateGoalRequest updateGoalRequest = UpdateGoalRequest.builder().id(999L).build();
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(updateGoalRequest));
    }

    @Test
    void deleteGoal_Success() {
        Goal goal = new Goal();
        goal.setId(1L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        goalService.deleteGoal(1L);
        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    void deleteGoal_ShouldThrowEntityNotFoundExceptionWhenGoalNotFound() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.deleteGoal(2L));
        verify(goalRepository, never()).delete(any(Goal.class));
    }

    @Test
    void getGoals_Success() {
        long userId = 10L;
        GoalFilterDto filterDto = GoalFilterDto.builder().build();
        List<Goal> goals = new ArrayList<>();
        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Test Goal");
        goals.add(goal1);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        GoalFilter filter = mock(GoalFilter.class);
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenAnswer(invocation -> invocation.getArgument(0));

        List<GoalFilter> mockFilters = List.of(filter);
        ReflectionTestUtils.setField(goalService, "filters", mockFilters);

        List<GoalResponse> result = goalService.getGoals(userId, filterDto);
        assertEquals(1, result.size());
        assertEquals("Test Goal", result.get(0).title());
    }

    @Test
    void getGoals_ShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.getGoals(userId, GoalFilterDto.builder().build()));
    }

    @Test
    void getSubtasksGoal_Success() {
        long goalId = 20L;
        Goal parentGoal = new Goal();
        parentGoal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(parentGoal));

        Goal subGoal1 = new Goal();
        subGoal1.setId(21L);
        Goal subGoal2 = new Goal();
        subGoal2.setId(22L);

        List<Goal> subGoals = List.of(subGoal1, subGoal2);
        when(goalRepository.findByParent(goalId)).thenReturn(subGoals.stream());

        List<GoalResponse> result = goalService.getSubtasksGoal(goalId);
        assertEquals(2, result.size());
        assertEquals(21L, result.get(0).id());
        assertEquals(22L, result.get(1).id());
    }

    @Test
    void getSubtasksGoal_ShouldThrowEntityNotFoundExceptionWhenParentGoalNotFound() {
        when(goalRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.getSubtasksGoal(30L));
    }

    @Test
    void removeUserFromGoal_MoreThan1UsersSuccess() {
        Goal goal = Goal.builder().id(13L).users(List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build(),
                User.builder().id(4L).build()
        )).build();

        goalService.removeUserFromGoal(goal, 1L);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(goalCaptor.capture());

        Assertions.assertEquals(goalCaptor.getValue().getId(), goal.getId());
    }

    @Test
    void removeUserFromGoal_OneUserSuccess() {
        Goal goal = Goal.builder().id(13L).users(List.of(
                User.builder().id(1L).build()
        )).build();

        when(goalRepository.findById(13L)).thenReturn(Optional.of(goal));

        goalService.removeUserFromGoal(goal, 1L);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).delete(goalCaptor.capture());

        Assertions.assertEquals(goalCaptor.getValue().getId(), goal.getId());
    }

    @Test
    void removeUserFromGoal_UserNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            goalService.removeUserFromGoal(
                    Goal.builder().users(Collections.emptyList()).build(),
                    1L);
        });
    }
}
