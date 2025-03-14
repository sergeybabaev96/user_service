package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.TestGoalStatusFilter;
import school.faang.user_service.filter.goal.TestGoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @Spy
    private GoalMapperImpl goalMapper;

    private final List<GoalFilter> filters = List.of(new TestGoalTitleFilter(), new TestGoalStatusFilter());
    private final long userId = 0;
    private final long goalId = 0;
    private final String title = "title";
    private final String description = "description";
    private final Goal parent = Goal.builder().id(1L).build();

    private final Stream<Goal> subtasks = Stream.of(
            Goal.builder().status(GoalStatus.ACTIVE).title(title).build(),
            Goal.builder().status(GoalStatus.COMPLETED).title(title).build(),
            Goal.builder().title(title).build(),
            Goal.builder().status(GoalStatus.ACTIVE).title("some").build(),
            Goal.builder().status(GoalStatus.ACTIVE).title("    ").build(),
            Goal.builder().status(GoalStatus.ACTIVE).build());

    private final List<GoalDto> expectedGoalDtos =
            List.of(GoalDto.builder().status(GoalStatus.ACTIVE).title(title).skillIds(List.of()).build());

    private final List<Skill> skills = List.of(
            Skill.builder().id(0).build(),
            Skill.builder().id(1).build(),
            Skill.builder().id(2).build());

    private final List<User> users = List.of(
            User.builder().id(0L).build(),
            User.builder().id(1L).build(),
            User.builder().id(2L).build());

    private final Goal goal = Goal.builder()
            .id(goalId)
            .title(title)
            .description(description)
            .parent(parent)
            .users(users)
            .build();

    private final GoalDto goalDto = GoalDto.builder()
            .title(title)
            .description(description)
            .skillIds(skills.stream().map(Skill::getId).toList())
            .parentId(parent.getId())
            .status(GoalStatus.COMPLETED)
            .build();

    @BeforeEach
    public void setUp() {
        goalService = new GoalService(goalRepository, skillService, userService, goalMapper, filters);
        goal.setStatus(GoalStatus.ACTIVE);
        goalDto.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    public void testCreateGoal() {
        goal.setSkillsToAchieve(skills);
        Optional<Goal> createdOptionalGoal = Optional.of(goal);
        mockCreateGoalMethods(createdOptionalGoal);

        GoalDto createdGoal = goalService.createGoal(userId, goalDto);

        verifyCreateGoalMockMethods();

        assertEquals(createdGoal.getId(), createdOptionalGoal.get().getId());
        assertEquals(createdGoal.getParentId(), createdOptionalGoal.get().getParent().getId());
    }

    @Test
    public void testNotCreateGoalWhenUserGoalsOverLimit() {
        when(userService.isWithinGoalLimit(userId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> goalService.createGoal(userId, goalDto));
        verify(userService, times(1)).isWithinGoalLimit(userId);
    }

    @Test
    public void testNotCreateGoalWhenGoalParenNotExist() {
        when(userService.isWithinGoalLimit(userId)).thenReturn(true);
        when(goalRepository.existsById(goalDto.getParentId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, goalDto));
        verify(userService, times(1)).isWithinGoalLimit(userId);
        verify(goalRepository, times(1)).existsById(goalDto.getParentId());
    }

    @Test
    public void testNotCreateGoalWhenIsntAllSkillsExist() {
        when(userService.isWithinGoalLimit(userId)).thenReturn(true);
        when(goalRepository.existsById(goalDto.getParentId())).thenReturn(true);
        when(skillService.isAllSkillsExist(goalDto.getSkillIds())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, goalDto));
        verify(userService, times(1)).isWithinGoalLimit(userId);
        verify(goalRepository, times(1)).existsById(goalDto.getParentId());
        verify(skillService, times(1)).isAllSkillsExist(goalDto.getSkillIds());
    }

    @Test
    public void testNotCreateGoalWhenGoalNotExist() {
        mockCreateGoalMethods(Optional.empty());

        assertThrows(RuntimeException.class, () -> goalService.createGoal(userId, goalDto));
        verifyCreateGoalMockMethods();
    }

    @Test
    public void testUpdateGoal() {
        Goal updatedGoal = goal;
        updatedGoal.setSkillsToAchieve(skills);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(skillService.isAllSkillsExist(goalDto.getSkillIds())).thenReturn(true);
        when(goalRepository.save(goal)).thenReturn(updatedGoal);

        GoalDto updatedGoalDto = goalService.updateGoal(goalId, goalDto);

        verify(goalRepository, times(1)).findById(goalId);
        verify(skillService, times(1)).isAllSkillsExist(goalDto.getSkillIds());
        verify(skillService, times(getCountAssignSkillToUser())).assignSkillToUser(anyLong(), anyLong());
        verify(goalRepository, times(1)).removeSkillsFromGoal(goalId);
        verify(goalRepository, times(goalDto.getSkillIds().size())).addSkillToGoal(anyLong(), anyLong());
        verify(goalRepository, times(1)).save(goal);

        assertEquals(updatedGoal.getSkillsToAchieve().size(), updatedGoalDto.getSkillIds().size());
        IntStream.range(0, skills.size()).forEach(num ->
                assertEquals(updatedGoal.getSkillsToAchieve().get(num).getId(), updatedGoalDto.getSkillIds().get(num)));
    }

    @Test
    public void testNotUpdateGoalWhenUserNotExists() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    public void testNotUpdateWhenGoalIsCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(goalId, goalDto));
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    public void testNotUpdateWhenGoalDtoIsntCompleted() {
        goalDto.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(goalId, goalDto));
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    public void testNotUpdateWhenIsntAllSkillExists() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(skillService.isAllSkillsExist(goalDto.getSkillIds())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));
        verify(goalRepository, times(1)).findById(goalId);
        verify(skillService, times(1)).isAllSkillsExist(goalDto.getSkillIds());

    }

    @Test
    public void testDeleteGoal() {
        when(goalRepository.existsById(goalId)).thenReturn(true);
        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).existsById(goalId);
        verify(goalRepository, times(1)).removeSkillsFromGoal(goalId);
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void testNotDeleteWhenGoalNotExists() {
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.deleteGoal(goalId));
        verify(goalRepository, times(1)).existsById(goalId);
    }

    @Test
    public void testGetSubtasksByGoalId() {
        when(goalRepository.findByParent(goalId)).thenReturn(subtasks);
        List<GoalDto> filteredGoalDtos =
                goalService.getSubtasksByGoalId(goalId, GoalFilterDto.builder().titlePattern("some").build());

        assertLists(filteredGoalDtos);
    }

    @Test
    public void testGetZeroSubtasksByGoalId() {
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());
        List<GoalDto> filteredGoalDtos =
                goalService.getSubtasksByGoalId(goalId, GoalFilterDto.builder().titlePattern("some").build());

        assertTrue(filteredGoalDtos.isEmpty());
    }

    @Test
    public void getGoalsByUser() {
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(subtasks);
        List<GoalDto> filteredGoalDtos =
                goalService.getGoalsByUserId(userId, GoalFilterDto.builder().titlePattern("some").build());

        assertLists(filteredGoalDtos);
    }

    @Test
    public void getZeroGoalsByUser() {
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());
        List<GoalDto> filteredGoalDtos =
                goalService.getGoalsByUserId(userId, GoalFilterDto.builder().titlePattern("some").build());

        assertTrue(filteredGoalDtos.isEmpty());
    }

    private void assertLists(List<GoalDto> filteredGoalDtos) {
        assertEquals(expectedGoalDtos.size(), filteredGoalDtos.size());
        assertEquals(1, expectedGoalDtos.size());
        assertEquals(expectedGoalDtos.get(0), filteredGoalDtos.get(0));
    }

    private int getCountAssignSkillToUser() {
        return goal.getUsers().size() * goal.getSkillsToAchieve().size();
    }

    private void mockCreateGoalMethods(Optional<Goal> optionalGoal) {
        when(userService.isWithinGoalLimit(userId)).thenReturn(true);
        when(goalRepository.existsById(goalDto.getParentId())).thenReturn(true);
        when(skillService.isAllSkillsExist(goalDto.getSkillIds())).thenReturn(true);
        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(goal);
        when(goalRepository.findById(goalId)).thenReturn(optionalGoal);
    }

    private void verifyCreateGoalMockMethods() {
        verify(userService, times(1)).isWithinGoalLimit(userId);
        verify(goalRepository, times(1)).existsById(goalDto.getParentId());
        verify(skillService, times(1)).isAllSkillsExist(goalDto.getSkillIds());
        verify(goalRepository, times(1))
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        verify(goalRepository, times(goalDto.getSkillIds().size())).addSkillToGoal(anyLong(), anyLong());
        verify(goalRepository, times(1)).findById(goalId);
    }
}