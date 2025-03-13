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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    private final Long userId = 1L;
    private final Long goalId = 1L;
    private final GoalDto firstGoalDto = GoalDto.builder()
            .id(goalId)
            .title("title")
            .status(GoalStatus.ACTIVE)
            .build();
    private final GoalDto secondGoalDto = GoalDto.builder()
            .id(2L)
            .title("   ")
            .status(GoalStatus.COMPLETED)
            .build();
    private final List<GoalDto> expectedGoals = List.of(firstGoalDto);
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

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
        when(goalService.findSubtasksByGoalId(goalId, searchGoalDto)).thenReturn(expectedGoals);

        List<GoalDto> actualGoals = goalController.findSubtasksByGoalId(goalId, searchGoalDto);

        verify(goalService, times(1)).findSubtasksByGoalId(goalId, searchGoalDto);
        assertEqualsLists(expectedGoals, actualGoals);
    }

    @Test
    public void testPositiveGetGoalsByUserId() {
        when(goalService.getGoalsByUserId(userId, searchGoalDto)).thenReturn(expectedGoals);

        List<GoalDto> actualGoals = goalController.getGoalsByUserId(userId, searchGoalDto);

        verify(goalService, times(1)).getGoalsByUserId(userId, searchGoalDto);
        assertEqualsLists(expectedGoals, actualGoals);
    }

    private void assertEqualsLists(List<GoalDto> expectedGoals, List<GoalDto> actualGoals) {
        assertEquals(1, actualGoals.size());
        assertEquals(expectedGoals, actualGoals);
    }
}
