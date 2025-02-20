package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.enums.goal.GoalStatus;
import school.faang.user_service.exception.MaxActiveGoalsLimitExceededException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.kafka.goal.GoalCompletedEventKafkaProducer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.goal.GoalPrepareData.getGoalDto;
import static school.faang.user_service.utils.goal.GoalPrepareData.getGoalDtoWithSkills;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalCompletedEventKafkaProducer goalCompletedEventKafkaProducer;

    @Spy
    private GoalMapperImpl goalMapper;

    private GoalServiceImpl goalService;

    private final List<Filter<Goal, GoalFilterDto>> goalFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        goalFilters.add(new GoalTitleFilter());
        goalService = new GoalServiceImpl(goalRepository, userRepository, skillRepository, goalMapper, goalFilters, goalCompletedEventKafkaProducer);
    }

    @Test
    public void testCreateGoal() {
        User user = new User();
        user.setId(1L);
        GoalDto goalDto = GoalDto.builder()
                .title("title")
                .description("descr")
                .skillIds(Arrays.asList(1L))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(0);
        when(skillRepository.existsById(eq(1L))).thenReturn(true);
        when(goalRepository.create("title", "descr", null)).thenReturn(
                Goal.builder().id(1L).build());

        GoalDto result = goalService.createGoal(1L, goalDto);

        assertNotNull(result);
        verify(goalRepository).create("title", "descr", null);
        verify(goalRepository).assignGoalToUser(anyLong(), eq(1L));
    }

    @Test
    public void testCreateGoalWhenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(1L, getGoalDto()));
    }

    @Test
    public void testCreateGoalWithMaxActiveGoals() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(goalRepository.countActiveGoalsPerUser(eq(1L))).thenReturn(3);

        assertThrows(MaxActiveGoalsLimitExceededException.class, () -> goalService.createGoal(userId, getGoalDto()));
    }

    @Test
    public void testUpdateGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(goal);
        when(skillRepository.existsById(eq(1L))).thenReturn(true);
        when(skillRepository.existsById(eq(2L))).thenReturn(true);

        GoalDto result = goalService.updateGoal(1L, getGoalDtoWithSkills());

        assertNotNull(result);
        verify(goalRepository).save(goal);
    }

    @Test
    public void testUpdateGoalWhenAlreadyCompleted() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(IllegalStateException.class, () -> goalService.updateGoal(1L, getGoalDtoWithSkills()));
    }

    @Test
    public void testDeleteGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        when(goalRepository.findByParent(1L)).thenReturn(List.of());
        doNothing().when(goalRepository).deleteById(eq(1L));

        goalService.deleteGoalById(1L);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    public void testFindSubgoalsByGoalId() {
        Goal subtask1 = new Goal();
        subtask1.setId(2L);
        Goal subtask2 = new Goal();
        subtask2.setId(3L);
        when(goalRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(Goal.builder().id(1L).build()));
        when(goalRepository.findByParent(1L)).thenReturn(List.of(subtask1, subtask2));

        List<GoalDto> result = goalService.findSubgoalsByGoalId(1L, new GoalFilterDto());

        assertEquals(2, result.size());
    }

    @Test
    public void testGetGoalsByUser() {
        Goal goal1 = new Goal();
        goal1.setTitle("Goal 1");
        goal1.setStatus(GoalStatus.ACTIVE);
        Goal goal2 = new Goal();
        goal2.setTitle("Goal 2");
        goal2.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(List.of(goal1, goal2));
        GoalFilterDto filter = new GoalFilterDto("Goal 1", GoalStatus.ACTIVE);

        List<GoalDto> result = goalService.findGoalsByUser(1L, filter);

        assertEquals(1, result.size());
        assertEquals("Goal 1", result.get(0).getTitle());
    }
}
