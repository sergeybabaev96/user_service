package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalTitleFilterTest {
    private final GoalTitleFilter goalTitleFilter = new GoalTitleFilter();

    @Test
    public void testApply() {
        GoalFilterDto filter = GoalFilterDto.builder().titlePattern("some").build();
        Stream<Goal> introductory = Stream.of(
                Goal.builder().title("some").description("first").build(),
                Goal.builder().title("something").description("second").build(),
                Goal.builder().title("som").description("third").build(),
                Goal.builder().title("").description("fourth").build(),
                Goal.builder().title("  ").description("fifth").build(),
                Goal.builder().build(),
                null
        );

        Stream<Goal> excepted = Stream.of(
                Goal.builder().title("some").description("first").build(),
                Goal.builder().title("something").description("second").build()
        );

        assertEquals(excepted.collect(Collectors.toSet()),
                goalTitleFilter.apply(introductory, filter).collect(Collectors.toSet()));
    }

    @Test
    public void testApplyEmptyStream() {
        GoalFilterDto filter = GoalFilterDto.builder().titlePattern("some").build();
        Stream<Goal> introductory = Stream.of();
        int excepted = 0;

        assertEquals(excepted, goalTitleFilter.apply(introductory, filter).toList().size());
    }

    @Test
    public void testNotApplyWithNullStream() {
        GoalFilterDto filter = GoalFilterDto.builder().titlePattern("some").build();

        assertNull(goalTitleFilter.apply(null, filter));
    }

    @Test
    public void testNotApplyWithBlankDescriptionPattern() {
        testNotApplyWithFilter(GoalFilterDto.builder().descriptionPattern("     ").build());
    }

    @Test
    public void testNotApplyWithNullDescriptionPattern() {
        testNotApplyWithFilter(GoalFilterDto.builder().build());
    }

    @Test
    public void testNotApplyWithENullFilter() {
        testNotApplyWithFilter(null);
    }

    private void testNotApplyWithFilter(GoalFilterDto filter) {
        Stream<Goal> introductory = Stream.of(Goal.builder().title("first").description("some").build());
        assertEquals(introductory, goalTitleFilter.apply(introductory, filter));
    }

}