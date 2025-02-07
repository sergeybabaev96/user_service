package school.faang.user_service.filter.goal.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TitleFilterTest {

    private TitleFilter titleFilter;
    private GoalFilterDto filter;
    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    void setup() {
        titleFilter = new TitleFilter();

        filter = new GoalFilterDto();
        filter.setTitle("Goal");

        goal1 = new Goal();
        goal1.setTitle("Goal 1");

        goal2 = new Goal();
        goal2.setTitle("Task 2");
    }

    @Test
    void testIsApplicable_ShouldReturnTrueIfTitleIsNotEmpty() {
        assertTrue(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_ShouldReturnFalseIfTitleIsNull() {
        filter.setTitle(null);
        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_ShouldReturnFalseIfTitleIsEmpty() {
        filter.setTitle("");
        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testApply_ShouldFilterGoalsByTitle() {
        List<Goal> filteredGoals = titleFilter.apply(Stream.of(goal1, goal2), filter)
                .toList();

        assertEquals(1, filteredGoals.size());
        assertEquals("Goal 1", filteredGoals.get(0).getTitle());
    }
}