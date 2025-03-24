package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalStatusFilterTest {

    private GoalStatusFilter filter;
    private GoalFilterDto goalFilterDto;

    @BeforeEach
    void setUp() {
        goalFilterDto = new GoalFilterDto();
        filter = new GoalStatusFilter();
        goalFilterDto.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    @DisplayName("Фильтр без статуса")
    void isApplicableNullDescription() {
        goalFilterDto.setStatus(null);

        boolean result = filter.isApplicable(goalFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с валидными данными")
    void isApplicableTrue() {
        boolean result = filter.isApplicable(goalFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра")
    void testApplyFilter() {
        Stream<Goal> goals = Stream.of(
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.COMPLETED).build()
        );

        Stream<Goal> filteredGoals = filter.apply(goals, goalFilterDto);
        List<Goal> filteredGoalsList = filteredGoals.toList();

        assertEquals(1, filteredGoalsList.size());
    }

    @Test
    @DisplayName("Успешное применение фильтра к пустому списку")
    void testApplyFilterEmptyStream() {
        Stream<Goal> goals = Stream.of();

        Stream<Goal> filteredGoals = filter.apply(goals, goalFilterDto);
        List<Goal> filteredGoalsList = filteredGoals.toList();

        assertEquals(0, filteredGoalsList.size());
    }
}