package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventLocationFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventLocationFilterTest {
    private EventLocationFilter eventLocationFilter = new EventLocationFilter();
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр применим, если location установлен и не пустой")
    void testIsApplicableTestTrue() {
        eventFilterDto.setLocation("Moscow");
        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр не применим, если location равен null")
    void testIsApplicableTestFalse() {
        eventFilterDto.setLocation(null);
        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр не применим, если location состоит только из пробелов")
    void testIsApplicableTestBlank() {
        eventFilterDto.setLocation(" ");
        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Тест isApplicable: фильтр не применим, если location пустой")
    void testIsApplicableTestEmpty() {
        eventFilterDto.setLocation("");
        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Тест apply: фильтрация событий по точному совпадению location")
    void applyTest() {
        eventFilterDto.setLocation("Moscow");
        Event event1 = Event.builder().location("Moscow").build();
        Event event2 = Event.builder().location("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);
        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);
        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Тест apply: фильтрация событий по location с учетом регистра")
    void applyIgnoreCaseTest() {
        eventFilterDto.setLocation("Moscow");
        Event event1 = Event.builder().location("moScow").build();
        Event event2 = Event.builder().location("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);
        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);
        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Тест apply: фильтрация пустого списка событий")
    void applyEmptyTest() {
        eventFilterDto.setLocation("Moscow");
        Stream<Event> events = Stream.empty();
        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);
        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Тест apply: фильтрация событий с пустым location")
    void applyTestEmptyLocation() {
        eventFilterDto.setLocation("Moscow");
        Event event = Event.builder().location("").build();
        Stream<Event> events = Stream.of(event);
        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);
        assertEquals(0, result.count());
    }
}
