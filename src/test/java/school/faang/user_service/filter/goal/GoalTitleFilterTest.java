package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoalTitleFilterTest {
    private final GoalTitleFilter filter = new GoalTitleFilter();
    private final Goal firstGoal = new Goal();
    private final Goal secondGoal = new Goal();
    private final Goal thirdGoal = new Goal();
    private final Goal fourthGoal = new Goal();
    private final String firstTitle = "title";
    private final String thirdTitle = "tiTlE";
    private final String fourthTitle = "Title";

    @BeforeEach
    public void setUp() {
        String secondTitle = "another title";
        firstGoal.setTitle(firstTitle);
        secondGoal.setTitle(secondTitle);
        thirdGoal.setTitle(thirdTitle);
        fourthGoal.setTitle(fourthTitle);
    }

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto("title", null));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableNullTitle() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto(null, null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableEmptyTitle() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto("", null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableBlankTitle() {
        boolean isApplicable = filter.isApplicable(new SearchGoalDto("      ", null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplyTitleExists() {
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto(firstTitle, null)).toList();

        assertEquals(1, filteredGoals.size());
        assertEquals(firstTitle, filteredGoals.get(0).getTitle());
    }

    @Test
    public void testPositiveApplyTitleDoesntExist() {
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto("name", null)).toList();

        assertEquals(0, filteredGoals.size());
    }

    @Test
    public void testPositiveApplyIgnoreCase() {
        Stream<Goal> goals = Stream.of(firstGoal, thirdGoal, fourthGoal);

        List<Goal> filteredGoals = filter.apply(goals, new SearchGoalDto(firstTitle, null)).toList();

        assertEquals(3, filteredGoals.size());
        assertEquals(firstTitle.toLowerCase(), filteredGoals.get(0).getTitle().toLowerCase());
        assertEquals(thirdTitle.toLowerCase(), filteredGoals.get(1).getTitle().toLowerCase());
        assertEquals(fourthTitle.toLowerCase(), filteredGoals.get(2).getTitle().toLowerCase());
    }
}
