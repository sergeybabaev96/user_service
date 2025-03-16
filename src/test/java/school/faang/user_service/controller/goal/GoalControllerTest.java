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
    private final Long firstGoalId = 1L;
    private final Long secondGoalId = 2L;
    private final List<GoalDto> expectedGoals = List.of(createGoal(firstGoalId, "title", GoalStatus.ACTIVE));
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    @Test
    public void testNegativeCreateGoalWhenBlankTitle() {
        GoalDto goalDto = createGoal(secondGoalId, "   ", GoalStatus.COMPLETED);
        assertThrows(IllegalArgumentException.class, () -> goalController.createGoal(userId, goalDto));
    }

    @Test()
    public void testPositiveCreateGoal() {
        GoalDto goalDto = createGoal(firstGoalId, "title", GoalStatus.ACTIVE);
        goalController.createGoal(userId, goalDto);

        verify(goalService, times(1)).createGoal(userId, goalDto);
    }

    @Test()
    public void testPositiveDeleteGoal() {
        goalController.deleteGoal(firstGoalId);

        verify(goalService, times(1)).deleteGoal(firstGoalId);
    }

    @Test
    public void testNegativeUpdateGoalWhenBlankTitle() {
        GoalDto goalDto = createGoal(secondGoalId, "   ", GoalStatus.COMPLETED);
        assertThrows(IllegalArgumentException.class, () -> goalController.updateGoal(firstGoalId, goalDto));
    }

    @Test()
    public void testPositiveUpdateGoal() {
        GoalDto goalDto = createGoal(firstGoalId, "title", GoalStatus.ACTIVE);
        goalController.updateGoal(firstGoalId, goalDto);

        verify(goalService, times(1)).updateGoal(firstGoalId, goalDto);
    }

    @Test()
    public void testPositiveFindSubtasksByGoalId() {
        when(goalService.findSubtasksByGoalId(firstGoalId, searchGoalDto)).thenReturn(expectedGoals);

        List<GoalDto> actualGoals = goalController.findSubtasksByGoalId(firstGoalId, searchGoalDto);

        verify(goalService, times(1)).findSubtasksByGoalId(firstGoalId, searchGoalDto);
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

    private GoalDto createGoal(Long id, String title, GoalStatus status) {
        return GoalDto.builder()
                .id(id)
                .title(title)
                .status(status)
                .build();
    }
}
