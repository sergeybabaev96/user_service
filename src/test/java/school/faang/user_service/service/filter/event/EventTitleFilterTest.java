package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventTitleFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventTitleFilterTest {
    private EventTitleFilter eventTitleFilter = new EventTitleFilter();
    private EventFilterDto eventFilterDto;

    @BeforeEach
    public void setUp() {
        eventFilterDto = new EventFilterDto();
    }

    @Test
    @DisplayName("Фильтр применим, если title установлен и не пустой")
    public void isApplicableTestTrue() {
        eventFilterDto.setTitle("Moscow");

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если title равен null")
    void isApplicableTestFalse() {
        eventFilterDto.setTitle(null);

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если title состоит только из пробелов")
    void isApplicableTestBlank() {
        eventFilterDto.setTitle(" ");

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если title пустой")
    void isApplicableTestEmpty() {
        eventFilterDto.setTitle("");

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтрация событий по точному совпадению title")
    void applyTest() {
        eventFilterDto.setTitle("Moscow");
        Event event1 = Event.builder().title("Moscow").build();
        Event event2 = Event.builder().title("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventTitleFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий по title с учетом регистра")
    void applyIgnoreCaseTest() {
        eventFilterDto.setTitle("Moscow");
        Event event1 = Event.builder().title("moScow").build();
        Event event2 = Event.builder().title("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventTitleFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация пустого списка событий")
    void applyEmptyTest() {
        eventFilterDto.setTitle("Moscow");
        Stream<Event> events = Stream.empty();

        Stream<Event> result = eventTitleFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с title, равным null")
    void applyTestNullTitle() {
        eventFilterDto.setTitle("Moscow");
        Event event = Event.builder().title(null).build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventTitleFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с пустым title")
    void applyTestEmptyTitle() {
        eventFilterDto.setTitle("Moscow");
        Event event = Event.builder().title("").build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventTitleFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());

    }
}
