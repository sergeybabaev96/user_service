package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventStartDateFilter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventStartDateFilterTest {
    private EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        eventFilterDto.setStartDate(LocalDateTime.parse("2021-12-12T12:12:12"));
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр применим, если startDate установлен")
    void isApplicableTestTrue() {
        boolean result = eventStartDateFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр не применим, если startDate не установлен")
    void isApplicableTestFalse() {
        eventFilterDto.setStartDate(null);
        boolean result = eventStartDateFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Тест apply: фильтрация событий с startDate, равным граничному значению")
    void applyTestBoundary() {
        Event event = Event.builder().startDate(LocalDateTime.parse("2021-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event);
        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);
        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Тест apply: фильтрация событий с startDate до и после указанной даты")
    void applyTest() {
        Event event1 = Event.builder().startDate(LocalDateTime.parse("2019-12-12T12:12:12")).build();
        Event event2 = Event.builder().startDate(LocalDateTime.parse("2024-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event1, event2);
        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);
        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Тест apply: фильтрация пустого списка событий")
    void applyTestEmpty() {
        Stream<Event> events = Stream.empty();
        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);
        assertEquals(0, result.count());
    }
}
