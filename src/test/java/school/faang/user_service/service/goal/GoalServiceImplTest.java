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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static school.faang.user_service.utils.goal.GoalPrepareData.*;

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
    public void testCreateGoal() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(getUser(userId)));
        when(goalRepository.create(getGoalDto().title(), getGoalDto().description(),
                getGoalDto().parent())).thenReturn(getGoalFromDto(getGoalDto()));
        when(goalMapper.toDto(getGoalFromDto(getGoalDto()))).thenReturn(getGoalDto());

        GoalDto result = goalService.createGoal(userId, getGoalDto());

        assertNotNull(result);
        assertEquals(getGoalDto().title(), result.title());
        assertEquals(getGoalDto().description(), result.description());
        verify(userRepository).findById(userId);
        verify(goalRepository).create(getGoalDto().title(), getGoalDto().description(), getGoalDto().parent());
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
    public void testUpdateGoal_Success() {
        long goalId = 1L;
        Goal updatedGoal = getUpdatedGoal(goalId, getExpectedGoalDto());
        GoalDto expectedGoalDto = getExpectedGoalDto();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(getExistingGoal(goalId)));
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.save(updatedGoal)).thenReturn(updatedGoal);

        GoalDto result = goalService.updateGoal(goalId, getExpectedGoalDto());

        assertNotNull(result);
        assertEquals(expectedGoalDto.title(), result.title());
        assertEquals(expectedGoalDto.description(), result.description());

        verify(goalRepository, times(1)).findById(goalId);
        verify(skillRepository, times(1)).findAllById(getExpectedGoalDto().skillIds());
        verify(goalMapper, times(1)).update(getExpectedGoalDto(), getExistingGoal(1L));
        verify(goalRepository, times(1)).save(updatedGoal);
        verify(goalMapper, times(1)).toDto(updatedGoal);
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        // Подготовка данных
        long goalId = 1L;
        GoalDto goalDto = new GoalDto("Updated Goal", "Updated Description", null, GoalStatus.ACTIVE, List.of(1L, 2L), null);

        // Мокирование
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        // Вызов метода и проверка исключения
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            goalService.updateGoal(goalId, goalDto);
        });

        assertEquals(String.format("Goal with id = %s not found", goalId), exception.getMessage());

        verify(goalRepository, times(1)).findById(goalId);
        verify(skillRepository, never()).findAllById(any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    public void testUpdateGoal_CheckCompletedStatus() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto("Updated Goal", "Updated Description", null, GoalStatus.COMPLETED, List.of(1L, 2L), null);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(getExistingGoal(goalId)));

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));

        verify(goalRepository).findById(goalId);
        verify(skillRepository, never()).findAllById(any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    public void testUpdateGoal_CheckExistsSkills() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto("Updated Goal", "Updated Description", null, GoalStatus.ACTIVE, List.of(1L, 2L), null);
        Goal existingGoal = getExistingGoal(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));

        verify(goalRepository, times(1)).findById(goalId);
        verify(skillRepository, never()).findAllById(any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    public void testUpdateGoal_UpdateUserSkillsIfCompleted() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto("Updated Goal", "Updated Description", null, GoalStatus.COMPLETED, List.of(1L, 2L), null);
        Goal existingGoal = getExistingGoal(goalId);
        existingGoal.setUsers(List.of(new User(), new User()));
        List<Skill> skills = List.of(new Skill(), new Skill());
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(goalDto.skillIds())).thenReturn(skills);

        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(goalId, goalDto));

        verify(goalRepository).findById(goalId);
        verify(skillRepository).findAllById(goalDto.skillIds());
    }
}
