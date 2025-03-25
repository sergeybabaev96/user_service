package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventLocationFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Тесты для проверки фильтрации сущности {@link EventLocationFilter}.
 */
@DisplayName("Проверка фильтрации событий по местоположению")
@ExtendWith(MockitoExtension.class)
public class EventLocationFilterTest {
    private EventLocationFilter eventLocationFilter;
    private EventFilterDto eventFilterDto;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        eventLocationFilter = new EventLocationFilter();
    }

    @Test
    @DisplayName("Фильтр применим, если location установлен и не пустой")
    void testIsApplicableTestTrue() {
        eventFilterDto.setLocation("Moscow");

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если location равен null")
    void testIsApplicableTestFalse() {
        eventFilterDto.setLocation(null);

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если location состоит только из пробелов")
    void testIsApplicableTestBlank() {
        eventFilterDto.setLocation(" ");

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если location пустой")
    void testIsApplicableTestEmpty() {
        eventFilterDto.setLocation("");

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтрация событий по точному совпадению location")
    void applyTest() {
        eventFilterDto.setLocation("Moscow");
        Event event1 = Event.builder().location("Moscow").build();
        Event event2 = Event.builder().location("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий по location с учетом регистра")
    void applyIgnoreCaseTest() {
        eventFilterDto.setLocation("Moscow");
        Event event1 = Event.builder().location("moScow").build();
        Event event2 = Event.builder().location("Gomel").build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация пустого списка событий")
    void applyEmptyTest() {
        eventFilterDto.setLocation("Moscow");
        Stream<Event> events = Stream.empty();

        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с пустым location")
    void applyTestEmptyLocation() {
        eventFilterDto.setLocation("Moscow");
        Event event = Event.builder().location("").build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventLocationFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Сервис возвращает отфильтрованные события по location")
    void getEventsByFilterShouldReturnFilteredEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .location("Moscow")
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .location("Saint Petersburg")
                .build();

        Event event3 = Event.builder()
                .id(3L)
                .location("Moscow")
                .build();

        Event event4 = Event.builder()
                .id(4L)
                .location("Kazan")
                .build();

        EventLocationFilter eventLocationFilter = new EventLocationFilter();

        List<EventFilter> eventFilters = List.of(eventLocationFilter);
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setLocation("Moscow");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        when(eventMapper.toDto(event1)).thenReturn(new EventViewDto());
        when(eventMapper.toDto(event3)).thenReturn(new EventViewDto());

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
