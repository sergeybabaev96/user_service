package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class GoalStatusFilterTest {

    private final GoalStatusFilter filter = new GoalStatusFilter();
    private final Goal firstGoal = Goal.builder()
            .status(GoalStatus.ACTIVE)
            .build();
    private final Goal secondGoal = Goal.builder()
            .status(GoalStatus.COMPLETED)
            .build();
    private final Goal thirdGoal = Goal.builder()
            .status(GoalStatus.ACTIVE)
            .build();

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto(null, GoalStatus.ACTIVE));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableNullStatus() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto(null, null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplyActiveStatus() {
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal, thirdGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto(null, GoalStatus.ACTIVE)).toList();

        assertEquals(2, filteredGoals.size());
    }

    @Test
    public void testPositiveApplyCompletedStatus() {
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal, thirdGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto(null, GoalStatus.COMPLETED)).toList();

        assertEquals(1, filteredGoals.size());
    }

    @Test
    public void testPositiveApplyNotFoundStatus() {
        Stream<Goal> goals = Stream.of(firstGoal, thirdGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto(null, GoalStatus.COMPLETED)).toList();

        assertEquals(0, filteredGoals.size());
    }
}
