package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.MaxActiveGoalsExceededException;
import school.faang.user_service.exception.NonExistentSkillException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.filters.goal.GoalFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal1;
    private Goal goal2;
    private Skill skill;
    private List<Goal> goals;
    private List<Skill> skills;
    private List<Skill> skillsNew;
    private List<Goal> subtasks;
    private List<User> users;
    private GoalFilterDto filters;
    private RequestGoalDto goalDto;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(1L)
                .title("Skill")
                .build();

        skills = new ArrayList<>();
        skills.add(skill);
        skillsNew = new ArrayList<>();
        skillsNew.add(skill);

        goal1 = Goal.builder()
                .id(1L)
                .title("Goal one")
                .description("Description one")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .parent(goal1)
                .title("Goal two")
                .description("Description two")
                .build();

        goals = new ArrayList<>();
        goals.add(goal2);
        goals.add(goal1);

        user = User.builder()
                .id(1L)
                .goals(goals)
                .build();
        users = new ArrayList<>();
        users.add(user);

        goal1.setUsers(users);

        subtasks = new ArrayList<>();
        subtasks.add(goal2);

        filters = new GoalFilterDto();
        filters.setTitle("two");

        goalDto = new RequestGoalDto();
        goalDto.setParentId(1L);
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setDeadline(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateGoal_UserNotFound() {
        when(userService.getUser(1L)).thenThrow(new NoSuchElementException("not found user with id 1"));
        assertThrows(NoSuchElementException.class, () -> goalService.createGoal(1L, goal1),
                "not found user with id 1");

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    public void testCreateGoal_ExceedsActiveGoals() {
        when(userService.getUser(1L)).thenReturn(user);
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(4);

        MaxActiveGoalsExceededException exception = assertThrows(MaxActiveGoalsExceededException.class, () ->
                goalService.createGoal(1L, goal1));

        assertEquals("The user's number of active goals exceeds the maximum number", exception.getMessage());
    }

    @Test
    public void testCreateGoal_NonExistentSkills() {
        when(userService.getUser(1L)).thenReturn(user);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(false);

        NonExistentSkillException exception = assertThrows(NonExistentSkillException.class, () ->
                goalService.createGoal(1L, goal1));

        assertEquals("The goal contains non-existent skills", exception.getMessage());
    }

    @Test
    public void testCreateGoal_Success() {
        when(userService.getUser(user.getId())).thenReturn(user);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(true);

        goalService.createGoal(user.getId(), goal1);

        verify(goalRepository, times(1)).save(goal1);
        assertEquals(GoalStatus.ACTIVE, goal1.getStatus());
        assertNotNull(goal1.getCreatedAt());
        assertTrue(goal1.getUsers().contains(user));
        assertTrue(user.getGoals().contains(goal1));
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalService.updateGoal(1L, goal1));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoalToActive() {
        goal1.setStatus(GoalStatus.ACTIVE);
        goal1.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(1L)).thenReturn(Optional.ofNullable(goal1));

        goalService.updateGoal(1L, goal1);

        verify(goalRepository, times(1)).save(goal1);
        assertEquals(GoalStatus.ACTIVE, goal1.getStatus());
    }

    @Test
    public void testUpdateGoalToCompleted() {
        goal1.setStatus(GoalStatus.ACTIVE);
        goal2.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(1L)).thenReturn(Optional.ofNullable(goal1));
        when(skillService.findSkillsByGoalId(1L)).thenReturn(skills);
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(users);

        goalService.updateGoal(1L, goal2);

        verify(goalRepository, times(1)).save(goal1);
        assertEquals(GoalStatus.COMPLETED, goal2.getStatus());
        verify(skillService, times(skills.size() * users.size())).assignSkillToGoal(anyLong(), anyLong());
    }

    @Test
    public void testDeleteGoal_GoalNotFound() {
        when(goalRepository.findById(4L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalService.deleteGoal(4L));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoal_Success() {
        when(goalRepository.findById(2L)).thenReturn(Optional.of(goal2));

        goalService.deleteGoal(2L);

        verify(goalRepository, times(1)).delete(goal2);
    }

    @Test
    public void testFindSubtasksByGoalId() {
        long parentId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockSubtasks = List.of(new Goal());
        when(goalRepository.findByParent(parentId)).thenReturn(mockSubtasks.stream());

        List<Goal> result = goalService.findSubtasksByGoalId(parentId, filters);

        assertEquals(mockSubtasks, result);
        verify(goalRepository, times(1)).findByParent(parentId);
    }

    @Test
    public void testGetGoalsByUserId() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockGoals = List.of(new Goal());
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(mockGoals.stream());

        List<Goal> result = goalService.getGoalsByUserId(userId, filters);

        assertEquals(mockGoals, result);
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void testUpdateSkillsToGoal() {
        long goalId = 1L;

        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(skills);

        goalService.updateSkillsToGoal(goalId, skillsNew);

        verify(goalRepository, times(1)).findSkillsByGoalId(goalId);
        skills.forEach(skill -> verify(skillService, times(1)).deleteSkill(skill));
        skillsNew.forEach(skill -> verify(skillService, times(1)).assignSkillToGoal(skill.getId(), goalId));
    }

    @Test
    public void testAssignSkillToGoal() {
        long skillId = 1L;
        long goalId = 2L;

        goalService.assignSkillToGoal(skillId, goalId);

        verify(skillService, times(1)).assignSkillToGoal(skillId, goalId);
    }
}