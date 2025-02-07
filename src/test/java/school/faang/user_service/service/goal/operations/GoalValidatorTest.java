package school.faang.user_service.service.goal.operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;

    private Long userId;
    private Long goalId;
    private Goal goal;
    private List<Long> skillIds;

    @InjectMocks
    private GoalValidator goalValidator;

    @BeforeEach
    void setup() {
        userId = 1L;
        goalId = 1L;
        skillIds = List.of(1L, 2L, 3L);

        goal = new Goal();
        goal.setId(goalId);
        goal.setStatus(GoalStatus.ACTIVE);
    }

    @Test
    void testValidateActiveGoalsLimit_ShouldThrowExceptionIfUserIdIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateActiveGoalsLimit(null));
        assertEquals("User ID cannot be null.", exception.getMessage());
    }

    @Test
    void testValidateActiveGoalsLimit_ShouldThrowExceptionIfActiveGoalsExceeded() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateActiveGoalsLimit(userId));

        assertEquals("User cannot have more than 3 active goals.", exception.getMessage());
        verify(goalRepository).countActiveGoalsPerUser(userId);
    }

    @Test
    void testValidateSkillsExist_ShouldThrowExceptionIfSkillIdsIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateSkillsExist(null));
        assertEquals("Skill list cannot be empty.", exception.getMessage());
    }

    @Test
    void testValidateSkillsExist_ShouldThrowExceptionIfSkillListIsEmpty() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateSkillsExist(List.of()));
        assertEquals("Skill list cannot be empty.", exception.getMessage());
    }

    @Test
    void testValidateSkillsExist_ShouldThrowExceptionIfSkillsDoNotExist() {
        when(skillRepository.countExisting(skillIds)).thenReturn(2);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateSkillsExist(skillIds));

        assertEquals("One or more skills do not exist.", exception.getMessage());
        verify(skillRepository).countExisting(skillIds);
    }

    @Test
    void testValidateSkillsExist_ShouldNotThrowExceptionIfSkillsExist() {
        when(skillRepository.countExisting(skillIds)).thenReturn(skillIds.size());

        assertDoesNotThrow(() -> goalValidator.validateSkillsExist(skillIds));
        verify(skillRepository).countExisting(skillIds);
    }

    @Test
    void testFindGoalById_ShouldThrowExceptionIfGoalIdIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.findGoalById(null));
        assertEquals("Goal ID cannot be null.", exception.getMessage());
    }

    @Test
    void testFindGoalById_ShouldThrowExceptionIfGoalNotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.findGoalById(goalId));

        assertEquals("Goal not found with id: " + goalId, exception.getMessage());
        verify(goalRepository).findById(goalId);
    }

    @Test
    void testFindGoalById_ShouldReturnGoalIfFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Goal result = goalValidator.findGoalById(goalId);

        assertNotNull(result);
        assertEquals(goal, result);
        verify(goalRepository).findById(goalId);
    }

    @Test
    void testFindParentGoal_ShouldReturnNullIfParentIdIsNull() {
        Goal parent = goalValidator.findParentGoal(null);
        assertNull(parent);
    }

    @Test
    void testFindParentGoal_ShouldReturnGoalIfParentIdExists() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Goal parent = goalValidator.findParentGoal(goalId);

        assertNotNull(parent);
        assertEquals(goal, parent);
        verify(goalRepository).findById(goalId);
    }

    @Test
    void testValidateGoalUpdatable_ShouldThrowExceptionIfGoalIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalUpdatable(null));
        assertEquals("Goal cannot be null.", exception.getMessage());
    }

    @Test
    void testValidateGoalUpdatable_ShouldThrowExceptionIfGoalIsCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalUpdatable(goal));

        assertEquals("Completed goals cannot be updated.", exception.getMessage());
    }

    @Test
    void testValidateGoalUpdatable_ShouldNotThrowExceptionForActiveGoal() {
        assertDoesNotThrow(() -> goalValidator.validateGoalUpdatable(goal));
    }
}