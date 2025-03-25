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
import school.faang.user_service.filter.event.EventStartDateFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Тесты для проверки фильтрации сущности {@link EventStartDateFilter}.
 */
@DisplayName("Проверка фильтрации событий по дате начала")
@ExtendWith(MockitoExtension.class)
public class EventStartDateFilterTest {
    private EventStartDateFilter eventStartDateFilter;
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
        eventFilterDto.setStartDate(LocalDateTime.parse("2021-12-12T12:12:12"));
        eventStartDateFilter = new EventStartDateFilter();
    }

    @Test
    @DisplayName("Фильтр применим, если startDate установлен")
    void isApplicableTestTrue() {
        boolean result = eventStartDateFilter.isApplicable(eventFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Фильтр не применим, если startDate не установлен")
    void isApplicableTestFalse() {
        eventFilterDto.setStartDate(null);

        boolean result = eventStartDateFilter.isApplicable(eventFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтрация событий с startDate, равным граничному значению")
    void applyTestBoundary() {
        Event event = Event.builder().startDate(LocalDateTime.parse("2021-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event);

        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Фильтрация событий с startDate до и после указанной даты")
    void applyTest() {
        Event event1 = Event.builder().startDate(LocalDateTime.parse("2019-12-12T12:12:12")).build();
        Event event2 = Event.builder().startDate(LocalDateTime.parse("2024-12-12T12:12:12")).build();
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Фильтрация пустого списка событий")
    void applyTestEmpty() {
        Stream<Event> events = Stream.empty();

        Stream<Event> result = eventStartDateFilter.apply(events, eventFilterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Сервис возвращает отфильтрованные события по startDate")
    void getEventsByFilterShouldReturnFilteredEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .startDate(LocalDateTime.parse("2020-12-12T12:12:12"))
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .startDate(LocalDateTime.parse("2022-12-12T12:12:12"))
                .build();

        Event event3 = Event.builder()
                .id(3L)
                .startDate(LocalDateTime.parse("2021-11-11T11:11:11"))
                .build();

        Event event4 = Event.builder()
                .id(4L)
                .startDate(LocalDateTime.parse("2022-12-12T12:12:12"))
                .build();

        EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();

        List<EventFilter> eventFilters = List.of(eventStartDateFilter);
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setStartDate(LocalDateTime.parse("2021-12-12T12:12:12"));

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        when(eventMapper.toDto(event2)).thenReturn(new EventViewDto());
        when(eventMapper.toDto(event4)).thenReturn(new EventViewDto());

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}