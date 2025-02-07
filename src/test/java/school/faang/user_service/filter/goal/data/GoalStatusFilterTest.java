package school.faang.user_service.filter.goal.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalStatusFilterTest {

    private GoalStatusFilter goalStatusFilter;
    private GoalFilterDto filter;
    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    void setup() {
        goalStatusFilter = new GoalStatusFilter();

        filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        goal1 = new Goal();
        goal1.setStatus(GoalStatus.ACTIVE);

        goal2 = new Goal();
        goal2.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    void testIsApplicable_ShouldReturnTrueIfStatusIsNotNull() {
        assertTrue(goalStatusFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_ShouldReturnFalseIfStatusIsNull() {
        filter.setStatus(null);
        assertFalse(goalStatusFilter.isApplicable(filter));
    }

    @Test
    void testApply_ShouldFilterGoalsByStatus() {
        List<Goal> filteredGoals = goalStatusFilter.apply(Stream.of(goal1, goal2), filter)
                .toList();

        assertEquals(1, filteredGoals.size());
        assertEquals(GoalStatus.ACTIVE, filteredGoals.get(0).getStatus());
    }
}