package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalDescriptionFilterTest {
    private final GoalDescriptionFilter goalDescriptionFilter = new GoalDescriptionFilter();

    @Test
    public void testApply() {
        GoalFilterDto filter = GoalFilterDto.builder().descriptionPattern("some").build();
        Stream<Goal> introductory = Stream.of(
                Goal.builder().title("first").description("some").build(),
                Goal.builder().title("second").description("something").build(),
                Goal.builder().title("third").description("som").build(),
                Goal.builder().title("fourth").description("").build(),
                Goal.builder().title("fifth").description("  ").build(),
                Goal.builder().title("sixth").build(),
                null
        );

        Stream<Goal> excepted = Stream.of(
                Goal.builder().title("first").description("some").build(),
                Goal.builder().title("second").description("something").build()
        );

        assertEquals(excepted.collect(Collectors.toSet()),
                goalDescriptionFilter.apply(introductory, filter).collect(Collectors.toSet()));
    }

    @Test
    public void testApplyEmptyStream() {
        GoalFilterDto filter = GoalFilterDto.builder().descriptionPattern("some").build();
        Stream<Goal> introductory = Stream.of();
        int excepted = 0;

        assertEquals(excepted, goalDescriptionFilter.apply(introductory, filter).toList().size());
    }

    @Test
    public void testNotApplyWithNullStream() {
        GoalFilterDto filter = GoalFilterDto.builder().descriptionPattern("some").build();

        assertNull(goalDescriptionFilter.apply(null, filter));
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
        assertEquals(introductory, goalDescriptionFilter.apply(introductory, filter));
    }
}