package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventEndDateFilter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventEndDateFilterTest {
    private EventEndDateFilter eventEndDateFilter = new EventEndDateFilter();
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setEndDate(LocalDateTime.parse("2021-12-12T12:12:12"));
    }

    @Test
    @DisplayName("Фильтр применим, если endDate установлен")
    void isApplicableTestTrue() {
        boolean result = eventEndDateFilter.isApplicable(eventFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если endDate не установлен")
    void isApplicableTestFalse() {
        eventFilterDto.setEndDate(null);

        boolean result = eventEndDateFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтрация событий с endDate, равным граничному значению")
    void applyTestBoundary() {
        Event event = Event.builder().endDate(LocalDateTime.parse("2021-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventEndDateFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с endDate до и после указанной даты")
    void applyTest() {
        Event event1 = Event.builder().endDate(LocalDateTime.parse("2019-12-12T12:12:12")).build();
        Event event2 = Event.builder().endDate(LocalDateTime.parse("2024-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventEndDateFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация пустого списка событий")
    void applyTestEmpty() {
        Stream<Event> events = Stream.empty();

        Stream<Event> result = eventEndDateFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с endDate, который раньше указанной даты")
    void applyTestBeforeEndDate() {
        Event event = Event.builder().endDate(LocalDateTime.parse("2020-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventEndDateFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с endDate, который позже указанной даты")
    void applyTestAfterEndDate() {
        Event event = Event.builder().endDate(LocalDateTime.parse("2022-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventEndDateFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }
}
