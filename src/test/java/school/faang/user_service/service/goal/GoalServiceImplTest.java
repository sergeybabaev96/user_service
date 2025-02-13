package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.goal.GoalPrepareData.getExistingGoal;
import static school.faang.user_service.utils.goal.GoalPrepareData.getExpectedGoalDto;
import static school.faang.user_service.utils.goal.GoalPrepareData.getGoalDto;
import static school.faang.user_service.utils.goal.GoalPrepareData.getGoalDtoWithSkills;
import static school.faang.user_service.utils.goal.GoalPrepareData.getUpdatedGoal;
import static school.faang.user_service.utils.goal.GoalPrepareData.getUser;
import static school.faang.user_service.utils.goal.GoalPrepareData.getUserWithMaxActiveGoals;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapperImpl goalMapper;

    private GoalServiceImpl goalService;

    private final List<Filter<Goal, GoalFilterDto>> goalFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        goalFilters.add(new GoalTitleFilter());
        goalService = new GoalServiceImpl(goalRepository, userRepository, skillRepository, goalMapper, goalFilters);
    }

    @Test
    public void testCreateGoalWhenUserNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, getGoalDto()));

        verify(userRepository).findById(userId);
        verify(goalRepository, never()).create(any(), any(), any());
    }

    @Test
    public void testCreateGoalWithMaxActiveGoals() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUserWithMaxActiveGoals(userId)));

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, getGoalDto()));

        verify(userRepository).findById(userId);
        verify(goalRepository, never()).create(any(), any(), any());
    }

    @Test
    public void testCreateGoalWenSkillNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(getUser(userId)));
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, getGoalDtoWithSkills()));

        verify(goalRepository, never()).create(any(), any(), any());
    }

    @Test
    public void testUpdateGoal() {
        long goalId = 1L;
        GoalDto expectedGoalDto = getExpectedGoalDto();
        when(goalRepository.findById(eq(goalId))).thenReturn(Optional.of(getUpdatedGoal(goalId)));
        when(skillRepository.existsById(eq(1L))).thenReturn(true);
        when(skillRepository.existsById(eq(2L))).thenReturn(true);
        when(skillRepository.findAllById(expectedGoalDto.skillIds())).thenReturn(
                List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.ofNullable(User.builder().id(2L).build()));
        doNothing().when(goalRepository).assignGoalToUser(eq(goalId), eq(2L));
        when(goalRepository.save(eq(getUpdatedGoal(goalId)))).thenReturn(getUpdatedGoal(goalId));

        GoalDto result = goalService.updateGoal(goalId, getExpectedGoalDto());

        assertNotNull(result);
        assertEquals(expectedGoalDto.title(), result.title());
        assertEquals(expectedGoalDto.description(), result.description());
        verify(goalRepository).findById(eq(goalId));
        verify(goalRepository).save(getUpdatedGoal(goalId));
    }

    @Test
    public void testUpdateGoalWhenGoalNotFound() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto(1L, "Updated Goal", "Updated Description", null, GoalStatus.ACTIVE, List.of(1L, 2L), null);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));

        verify(goalRepository, times(1)).findById(goalId);
        verify(skillRepository, never()).findAllById(any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    public void testUpdateGoalWhenAlreadyCompleted() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto(1L, "Updated Goal", "Updated Description", null, GoalStatus.COMPLETED, List.of(1L, 2L), null);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(getExistingGoal(goalId)));

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));

        verify(goalRepository).findById(goalId);
        verify(skillRepository, never()).findAllById(any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    public void testFindSubgoalsByGoalId_GoalFound_SubtasksExist() {
        Goal firstSubgoal = new Goal();
        firstSubgoal.setId(2L);
        firstSubgoal.setTitle("Subtask 1");
        Goal secondSubgoal = new Goal();
        secondSubgoal.setId(3L);
        secondSubgoal.setTitle("Subtask 2");
        when(goalRepository.findById(1L)).thenReturn(Optional.of(getExistingGoal(1L)));
        when(goalRepository.findByParent(1L)).thenReturn(List.of(firstSubgoal, secondSubgoal));

        List<GoalDto> result = goalService.findSubgoalsByGoalId(1L, GoalFilterDto.builder().build());

        assertEquals(2, result.size());
        assertEquals("Subtask 1", result.get(0).title());
        assertEquals("Subtask 2", result.get(1).title());
        verify(goalRepository).findById(1L);
        verify(goalRepository).findByParent(1L);
    }

    @Test
    public void testFindSubgoalsByGoalIdWhenFilter() {
        Goal firstSubgoal = new Goal();
        firstSubgoal.setId(2L);
        firstSubgoal.setTitle("Subgoal 1");
        Goal secondSubGoal = new Goal();
        secondSubGoal.setId(3L);
        secondSubGoal.setTitle("Subgoal 2");
        when(goalRepository.findById(1L)).thenReturn(Optional.of(getExistingGoal(1L)));
        when(goalRepository.findByParent(1L)).thenReturn(List.of(firstSubgoal, secondSubGoal));

        List<GoalDto> result = goalService.findSubgoalsByGoalId(1L, new GoalFilterDto("Subgoal 1", null));

        assertEquals(1, result.size());
        assertEquals("Subgoal 1", result.get(0).title());
        verify(goalRepository).findById(1L);
        verify(goalRepository).findByParent(1L);
    }
}
