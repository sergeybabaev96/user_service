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
import school.faang.user_service.filter.event.EventEndDateFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
 * Тесты для проверки фильтрации сущности {@link EventEndDateFilter}.
 */
@DisplayName("Проверка фильтрации событий по дате окончания")
@ExtendWith(MockitoExtension.class)
public class EventEndDateFilterTest {
    private EventEndDateFilter eventEndDateFilter;
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
        eventFilterDto.setEndDate(LocalDateTime.parse("2021-12-12T12:12:12"));
        eventEndDateFilter = new EventEndDateFilter();
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

    @Test
    @DisplayName("Сервис возвращает отфильтрованные события по endDate")
    void getEventsByFilterShouldReturnFilteredEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .endDate(LocalDateTime.parse("2020-12-12T12:12:12"))
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .endDate(LocalDateTime.parse("2022-12-12T12:12:12"))
                .build();

        Event event3 = Event.builder()
                .id(3L)
                .endDate(LocalDateTime.parse("2021-11-11T11:11:11"))
                .build();

        Event event4 = Event.builder()
                .id(4L)
                .endDate(LocalDateTime.parse("2021-10-10T10:10:10"))
                .build();

        EventEndDateFilter eventEndDateFilter = Mockito.mock(EventEndDateFilter.class);

        List<EventFilter> eventFilters = List.of(eventEndDateFilter);
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setEndDate(LocalDateTime.parse("2021-12-12T12:12:12"));

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        when(eventMapper.toDto(event1)).thenReturn(new EventViewDto());
        when(eventMapper.toDto(event3)).thenReturn(new EventViewDto());
        when(eventMapper.toDto(event4)).thenReturn(new EventViewDto());

        when(eventEndDateFilter.isApplicable(filter)).thenReturn(true);
        when(eventEndDateFilter.apply(any(), eq(filter))).thenAnswer(invocation -> {
            Stream<Event> inputStream = invocation.getArgument(0);
            return inputStream.filter(event -> event.getEndDate().isBefore(filter.getEndDate()));
        });

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(3, result.size());
    }
}