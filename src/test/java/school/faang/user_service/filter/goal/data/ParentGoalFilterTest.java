package school.faang.user_service.filter.goal.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParentGoalFilterTest {

    private ParentGoalFilter parentGoalFilter;
    private GoalFilterDto filter;
    private Goal goal1;
    private Goal goal2;
    private Goal parentGoal;

    @BeforeEach
    void setup() {
        parentGoalFilter = new ParentGoalFilter();

        parentGoal = new Goal();
        parentGoal.setId(1L);

        filter = new GoalFilterDto();
        filter.setParentId(1L);

        goal1 = new Goal();
        goal1.setParent(parentGoal);

        goal2 = new Goal();
        goal2.setParent(null);
    }

    @Test
    void testIsApplicable_ShouldReturnTrueIfParentIdIsNotNull() {
        assertTrue(parentGoalFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_ShouldReturnFalseIfParentIdIsNull() {
        filter.setParentId(null);
        assertFalse(parentGoalFilter.isApplicable(filter));
    }

    @Test
    void testApply_ShouldFilterGoalsByParentId() {
        List<Goal> filteredGoals = parentGoalFilter.apply(Stream.of(goal1, goal2), filter)
                .toList();

        assertEquals(1, filteredGoals.size());
        assertNotNull(filteredGoals.get(0).getParent());
        assertEquals(1L, filteredGoals.get(0).getParent().getId());
    }
}