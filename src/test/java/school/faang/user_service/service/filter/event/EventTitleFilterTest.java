package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Тесты для проверки фильтрации сущности {@link EventTitleFilter}.
 */
@DisplayName("Проверка фильтрации событий по названию")
@ExtendWith(MockitoExtension.class)
public class EventTitleFilterTest {
    private EventTitleFilter eventTitleFilter;
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
    public void setUp() {
        eventFilterDto = new EventFilterDto();
        eventTitleFilter = new EventTitleFilter();
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

    @Test
    @DisplayName("Сервис возвращает отфильтрованные события по title")
    void getEventsByFilterShouldReturnFilteredEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .title("Event 1")
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .title("Event 2")
                .build();

        Event event3 = Event.builder()
                .id(3L)
                .title("Special")
                .build();

        Event event4 = Event.builder()
                .id(4L)
                .title("Another")
                .build();

        EventTitleFilter eventTitleFilter = Mockito.mock(EventTitleFilter.class);

        List<EventFilter> eventFilters = List.of(eventTitleFilter);
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setTitle("Event");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        when(eventMapper.toDto(event1)).thenReturn(new EventViewDto());
        when(eventMapper.toDto(event2)).thenReturn(new EventViewDto());

        when(eventTitleFilter.isApplicable(filter)).thenReturn(true);
        when(eventTitleFilter.apply(any(), eq(filter))).thenAnswer(invocation -> {
            Stream<Event> inputStream = invocation.getArgument(0);
            return inputStream.filter(event -> event.getTitle().contains(filter.getTitle()));
        });

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}