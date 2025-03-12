package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private final Long userId = 1L;
    private final GoalDto firstGoalDto = new GoalDto(
            1L, "description", null, "title", GoalStatus.ACTIVE, List.of(1L, 2L, 3L));
    private final GoalDto secondGoalDto = new GoalDto(
            2L, "description", 1L, "  ", GoalStatus.COMPLETED, List.of(1L, 2L, 3L));
    private final GoalDto thirdGoalDto = new GoalDto(
            3L, "description", 1L, "title", GoalStatus.ACTIVE, List.of(1L, 2L, 3L));
    private final Long goalId = 1L;
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);

    @Test
    public void testNegativeCreateGoalWhenBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> goalController.createGoal(userId, secondGoalDto));
    }

    @Test()
    public void testPositiveCreateGoal() {
        goalController.createGoal(userId, firstGoalDto);

        verify(goalService, times(1)).createGoal(userId, firstGoalDto);
    }

    @Test()
    public void testPositiveDeleteGoal() {
        goalController.deleteGoal(goalId);
        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    public void testNegativeUpdateGoalWhenBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> goalController.updateGoal(goalId, secondGoalDto));
    }

    @Test()
    public void testPositiveUpdateGoal() {
        goalController.updateGoal(goalId, firstGoalDto);
        verify(goalService, times(1)).updateGoal(goalId, firstGoalDto);
    }

    @Test()
    public void testPositiveFindSubtasksByGoalId() {
        List<GoalDto> listGoals = goalController.findSubtasksByGoalId(goalId, searchGoalDto);

        verify(goalService, times(1)).findSubtasksByGoalId(goalId, searchGoalDto);
    }

    @Test
    public void testPositiveGetGoalsByUserId() {
        List<GoalDto> listGoals = goalController.getGoalsByUserId(userId, searchGoalDto);

        verify(goalService, times(1)).getGoalsByUserId(userId, searchGoalDto);
    }
}
