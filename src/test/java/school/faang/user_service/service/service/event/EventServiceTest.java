package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventLocationFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

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

    private EventCreateDto eventCreateDto;
    private EventViewDto eventViewDto;
    private User eventOwner;
    private List<Skill> requiredSkills;
    private Skill skill;
    private Event event;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(2L);

        requiredSkills = new ArrayList<>();
        requiredSkills.add(skill);

        eventOwner = new User();
        eventOwner.setId(123L);
        eventOwner.setSkills(requiredSkills);

        event = new Event();
        event.setId(2L);
        event.setTitle("Java Conference 2023");
        event.setStartDate(LocalDateTime.of(2023, 11, 15, 9, 0));
        event.setEndDate(LocalDateTime.of(2023, 11, 15, 18, 0));
        event.setOwner(eventOwner);
        event.setDescription("Annual conference for Java developers.");
        event.setRelatedSkills(requiredSkills);
        event.setLocation("New York");
        event.setMaxAttendees(500);
        event.setType(EventType.GIVEAWAY);

        eventCreateDto = new EventCreateDto();
        eventCreateDto.setTitle("Java Conference 2023");
        eventCreateDto.setStartDate(LocalDateTime.of(2023, 11, 15, 9, 0));
        eventCreateDto.setEndDate(LocalDateTime.of(2023, 11, 15, 18, 0));
        eventCreateDto.setOwnerId(123L);
        eventCreateDto.setDescription("Annual conference for Java developers.");
        eventCreateDto.setRelatedSkillsId(new ArrayList<>(List.of(2L)));
        eventCreateDto.setLocation("New York");
        eventCreateDto.setMaxAttendees(500);
        eventCreateDto.setEventType(EventType.GIVEAWAY);

        eventViewDto = new EventViewDto();
        eventViewDto.setId(2L);
        eventViewDto.setTitle("Java Conference 2023");
        eventViewDto.setStartDate(LocalDateTime.of(2023, 11, 15, 9, 0));
        eventViewDto.setEndDate(LocalDateTime.of(2023, 11, 15, 18, 0));
        eventViewDto.setOwnerId(123L);
        eventViewDto.setDescription("Annual conference for Java developers.");
        eventViewDto.setRelatedSkillsId(new ArrayList<>(List.of(2L)));
        eventViewDto.setLocation("New York");
        eventViewDto.setMaxAttendees(500);
        eventViewDto.setEventType(EventType.GIVEAWAY);
    }

    @Test
    @DisplayName("Создание события: успешный сценарий")
    void createEventSuccess() {
        when(userRepository.findById(eventCreateDto.getOwnerId())).thenReturn(Optional.of(eventOwner));
        when(skillRepository.findAllById(eventCreateDto.getRelatedSkillsId())).thenReturn(requiredSkills);
        when(eventMapper.toEntity(eventCreateDto)).thenReturn(event);
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        EventViewDto result = eventService.create(eventCreateDto);

        assertNotNull(result);
        assertEquals(eventViewDto, result);

        verify(userRepository, times(2)).findById(eventCreateDto.getOwnerId());
        verify(skillRepository, times(1)).findAllById(eventCreateDto.getRelatedSkillsId());
        verify(eventMapper, times(1)).toEntity(eventCreateDto);
        verify(eventRepository, times(1)).save(event);
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Валидация навыков: ошибка, если пользователь не найден")
    void validateUserSkillsUserNotFound() {
        when(userRepository.findById(eventCreateDto.getOwnerId())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.create(eventCreateDto));

        verify(userRepository, times(1)).findById(eventCreateDto.getOwnerId());
        verifyNoInteractions(skillRepository, eventMapper, eventRepository);
    }

    @Test
    @DisplayName("Валидация навыков: ошибка, если список навыков пуст")
    void validateUserSkillsSkillsListEmpty() {
        eventCreateDto.setRelatedSkillsId(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> eventService.create(eventCreateDto));

        verifyNoInteractions(userRepository, skillRepository, eventMapper, eventRepository);
    }

    @Test
    @DisplayName("Валидация навыков: ошибка, если список навыков null")
    void validateUserSkillsSkillsListNull() {
        eventCreateDto.setRelatedSkillsId(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventCreateDto));

        verifyNoInteractions(userRepository, skillRepository, eventMapper, eventRepository);
    }

    @Test
    @DisplayName("Валидация навыков: ошибка, если пользователь не обладает необходимыми навыками")
    void validateUserSkillsUserDoesNotHaveRequiredSkills() {
        List<Long> requiredSkillsIds = List.of(2L, 3L);
        eventCreateDto.setRelatedSkillsId(requiredSkillsIds);

        when(userRepository.findById(eventCreateDto.getOwnerId())).thenReturn(Optional.of(eventOwner));
        when(skillRepository.findAllById(requiredSkillsIds)).thenReturn(requiredSkills);

        assertThrows(NullPointerException.class, () -> eventService.create(eventCreateDto));

        verify(userRepository, times(2)).findById(eventCreateDto.getOwnerId());
        verify(skillRepository, times(1)).findAllById(requiredSkillsIds);
        verifyNoInteractions(eventRepository);
    }

    @Test
    @DisplayName("Получение события по ID: успешный сценарий")
    void getEventByIdSuccess() {
        when(eventRepository.findById(eventViewDto.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        EventViewDto result = eventService.getEvent(eventViewDto.getId());

        assertNotNull(result);
        assertEquals(eventViewDto, result);

        verify(eventRepository, times(1)).findById(eventViewDto.getId());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Получение события по ID: ошибка, если событие не найдено")
    void getEventByIdEventNotFound() {
        when(eventRepository.findById(eventViewDto.getId())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEvent(eventViewDto.getId()));

        verify(eventRepository, times(1)).findById(eventViewDto.getId());
        verifyNoInteractions(eventMapper);
    }

    @Test
    @DisplayName("Получение событий: фильтр передан, и один фильтр применим")
    void getEventsByFilterOneApplicableFilter() {
        List<EventFilter> eventFilters = new ArrayList<>(List.of(Mockito.mock(EventLocationFilter.class)));
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setLocation("New York");

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);
        when(eventFilters.get(0).isApplicable(filter)).thenReturn(true);

        when(eventFilters.get(0).apply(any(), eq(filter))).thenAnswer(invocation -> {
            Stream<Event> inputStream = invocation.getArgument(0);
            return inputStream.filter(e -> e.getLocation().equals(filter.getLocation()));
        });

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventViewDto, result.get(0));
    }

    @Test
    @DisplayName("Получение событий: фильтр передан, и фильтры корректно применяются")
    void getEventsByFilterMultipleApplicableFilters() {
        List<EventFilter> eventFilters = new ArrayList<>(List.of(
                Mockito.mock(EventLocationFilter.class),
                Mockito.mock(EventTitleFilter.class)));
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        EventFilterDto filter = new EventFilterDto();
        filter.setLocation("New York");
        filter.setTitle("Java Conference 2023");

        Event event1 = new Event();
        event1.setLocation("New York");
        event1.setTitle("Java Conference 2023");
        Event event2 = new Event();
        event2.setLocation("Minsk");
        event2.setTitle("Java Conference 2023");
        Event event3 = new Event();
        event3.setLocation("New York");
        event3.setTitle("Java Conference 2023");
        Event event4 = new Event();
        event4.setLocation("Minsk");
        event4.setTitle("C++ Conference 2025");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));
        when(eventMapper.toDto(event1)).thenReturn(new EventViewDto());

        when(eventFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(eventFilters.get(1).isApplicable(filter)).thenReturn(true);

        when(eventFilters.get(0).apply(any(), eq(filter))).thenAnswer(invocation -> {
            Stream<Event> inputStream = invocation.getArgument(0);
            return inputStream.filter(e -> e.getLocation().equals(filter.getLocation()));
        });

        when(eventFilters.get(1).apply(any(), eq(filter))).thenAnswer(invocation -> {
            Stream<Event> inputStream = invocation.getArgument(0);
            return inputStream.filter(e -> e.getTitle().equals(filter.getTitle()));
        });

        List<EventViewDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Получение событий: фильтр не передан null")
    void getEventsByFilterNullFilter() {
        List<EventFilter> eventFilters = new ArrayList<>(List.of(Mockito.mock(EventLocationFilter.class)));
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        List<EventViewDto> result = eventService.getEventsByFilter(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventViewDto, result.get(0));

        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Получение событий: фильтр передан, но ни один фильтр не применим")
    void getEventsByFilterNoApplicableFilters() {
        List<EventFilter> eventFilters = new ArrayList<>(List.of(Mockito.mock(EventLocationFilter.class)));
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        List<EventViewDto> result = eventService.getEventsByFilter(new EventFilterDto());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventViewDto, result.get(0));

        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Получение событий: фильтр передан, но события отсутствуют")
    void getEventsByFilterNoEvents() {
        List<EventFilter> eventFilters = new ArrayList<>(List.of(Mockito.mock(EventLocationFilter.class)));
        eventService = new EventService(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);

        when(eventRepository.findAll()).thenReturn(List.of());

        List<EventViewDto> result = eventService.getEventsByFilter(new EventFilterDto());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository, times(1)).findAll();
        verifyNoInteractions(eventMapper);
    }

    @Test
    @DisplayName("Удаление события: успешное удаление")
    void deleteEventSuccess() {
        long eventId = 123L;

        when(eventRepository.existsById(eventId)).thenReturn(true);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    @DisplayName("Удаление события: ошибка, событие не найдено")
    void deleteEventEventNotFound() {
        long eventId = 123L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.deleteEvent(eventId));

        assertEquals("Event not found with ID: " + eventId, exception.getMessage());

        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    @DisplayName("Получение событий пользователя: успешный сценарий")
    void getOwnerEventSuccess() {
        long userId = 123L;
        List<Event> events = List.of(event);

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        List<EventViewDto> result = eventService.getOwnerEvent(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventViewDto, result.get(0));

        verify(eventRepository, times(1)).findAllByUserId(userId);
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Получение событий пользователя: события отсутствуют")
    void getOwnerEventNoEvents() {
        long userId = 123L;

        when(eventRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<EventViewDto> result = eventService.getOwnerEvent(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Получение событий, в которых пользователь участвовал: успешный сценарий")
    void getParticipatedEventsSuccess() {
        long userId = 123L;
        List<Event> events = List.of(event);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        List<EventViewDto> result = eventService.getParticipatedEvents(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventViewDto, result.get(0));

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Получение событий, в которых пользователь участвовал: события отсутствуют")
    void getParticipatedEventsNoEvents() {
        long userId = 123L;

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(List.of());

        List<EventViewDto> result = eventService.getParticipatedEvents(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
    }

    @Test
    @DisplayName("Обновление события: успешное обновление")
    void updateEvent_Success() {
        skill.setEvents(new ArrayList<>(List.of(event)));

        when(eventMapper.toEntity(eventCreateDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventViewDto);

        EventViewDto result = eventService.updateEvent(eventCreateDto);

        assertNotNull(result);
        assertEquals(eventViewDto.getId(), result.getId());
        assertEquals(eventViewDto.getOwnerId(), result.getOwnerId());
        assertEquals(eventViewDto.getEventType(), result.getEventType());
        assertEquals(eventViewDto.getTitle(), result.getTitle());

        verify(eventMapper, times(1)).toEntity(eventCreateDto);
        verify(eventRepository, times(1)).save(event);
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("Обновление события: пользователь не имеет доступа")
    void updateEvent_UserHasNoAccess_ThrowsException() {
        skill.setEvents(new ArrayList<>(List.of(new Event())));

        when(eventMapper.toEntity(eventCreateDto)).thenReturn(event);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventCreateDto));
    }
}