package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Event.DateRangeFilter;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DateRangeFilterTest {
    private final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 10, 0);
    private final LocalDateTime END = LocalDateTime.of(2026, 10, 5, 18, 0);

    @Test
    void testMatchesEventWithinDateRange() {
        Event event = Event.builder()
                .startDate(START.plusDays(1))
                .endDate(END.minusDays(1))
                .build();
        DateRangeFilter filter = new DateRangeFilter(START, END);

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesEventStartsBeforeFilterStart() {
        Event event = Event.builder()
                .startDate(START.minusHours(1))
                .endDate(END)
                .build();
        DateRangeFilter filter = new DateRangeFilter(START, END);

        assertFalse(filter.matches(event));
    }

    @Test
    void testMatchesEventEndsAfterFilterEnd() {
        Event event = Event.builder()
                .startDate(START)
                .endDate(END.plusHours(1))
                .build();
        DateRangeFilter filter = new DateRangeFilter(START, END);

        assertFalse(filter.matches(event));
    }

    @Test
    void testMatchesWhenStartDateFilterIsNull() {
        Event event = Event.builder()
                .startDate(LocalDateTime.now())
                .endDate(END)
                .build();
        DateRangeFilter filter = new DateRangeFilter(null, END);

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesWhenEndDateFilterIsNull() {
        Event event = Event.builder()
                .startDate(START)
                .endDate(LocalDateTime.now())
                .build();
        DateRangeFilter filter = new DateRangeFilter(START, null);

        assertTrue(filter.matches(event));
    }
}
