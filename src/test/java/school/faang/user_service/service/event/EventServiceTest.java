package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.databuilder.event.EventBuilder;
import school.faang.user_service.databuilder.event.EventDtoBuilder;
import school.faang.user_service.databuilder.event.SkillBuilder;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.Event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private EventDto eventDto;
    private User validUser;
    private Event event;

    private static final long EVENT_ID = 1L;
    private static final long NON_EXISTING_EVENT_ID = 999L;
    private static final long USER_ID = 1L;
    private static final long SKILL_JAVA_ID = 10L;
    private static final long SKILL_SQL_ID = 20L;
    private static final LocalDateTime BASELINE_TIME = LocalDateTime.now().plusYears(1);

    @BeforeEach
    @DisplayName("Setup event DTO, valid user and event")
    void setUp() {
        eventDto = EventDtoBuilder.createValidEventDto(EVENT_ID);
        validUser = User.builder()
                .id(eventDto.getOwnerId())
                .skills(new ArrayList<>(List.of(
                        SkillBuilder.createValidSkill(SKILL_JAVA_ID, "Java"),
                        SkillBuilder.createValidSkill(SKILL_SQL_ID, "SQL")
                )))
                .build();
        event = EventBuilder.createValidEvent(EVENT_ID, validUser);
    }

    @Nested
    @DisplayName("Create Event Tests")
    class CreateEventTests {

        @Test
        @DisplayName("Creating a valid event returns a DTO with an assigned ID.")
        void testCreateValidEvent() {
            when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(validUser));
            when(eventMapper.toEntity(eventDto, skillRepository)).thenReturn(event);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(EventDtoBuilder.createValidEventDto(1L));

            EventDto result = eventService.create(eventDto);

            verify(eventMapper).toEntity(eq(eventDto), eq(skillRepository));
            verify(eventMapper).toDto(any(Event.class));
            verify(eventRepository).save(eventCaptor.capture());

            Event capturedEvent = eventCaptor.getValue();
            assertEquals(EVENT_ID, capturedEvent.getId());
            assertEquals(EVENT_ID, result.getId());
        }

        @Test
        @DisplayName("Creating an event for a non-existent user throws a DataValidationException.")
        void testCreateUserNotFound() {
            when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.create(eventDto)
            );

            assertAll (
                    () -> assertEquals("User not found", exception.getMessage()),
                    () -> verify(eventRepository, never()).save(any())
            );
        }

        @Test
        @DisplayName("Create event if user does not have required skills throws DataValidationException.")
        void testCreateUserLacksRequiredSkills() {
            validUser.getSkills().removeIf(skill -> skill.getId() == SKILL_JAVA_ID);
            when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(validUser));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.create(eventDto)
            );

            assertAll (
                    () -> assertEquals("User does not have the required skills for this event", exception.getMessage()),
                    () -> assertFalse(validUser.getSkills().stream().anyMatch(skill -> skill.getId() == SKILL_JAVA_ID),
                            "User should not have skill Java"),
                    () -> verify(eventRepository, never()).save(any())
            );
        }
    }

    @Nested
    @DisplayName("Update Event Tests")
    class UpdateEventTests {

        @Test
        @DisplayName("Updating an event with valid data changes the title and description.")
        void testUpdateValidData() {
            EventDto updatedDto = EventDto
                    .builder()
                    .id(EVENT_ID)
                    .ownerId(validUser.getId())
                    .title("Updated Title")
                    .description("New Description")
                    .relatedSkills(List.of(10L))
                    .build();

            when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
            when(eventRepository.getReferenceById(EVENT_ID)).thenReturn(event);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(updatedDto);

            EventDto result = eventService.updateEvent(updatedDto, EVENT_ID);

            assertAll(
                    () -> assertEquals("Updated Title", result.getTitle()),
                    () -> assertEquals("New Description", result.getDescription())
            );
        }

        @Test
        @DisplayName("Should not allow updating event with different ownerId")
        void testUpdateEventDifferentOwnerId() {
            EventDto eventDto = EventDto.builder().id(EVENT_ID).ownerId(USER_ID + 1).build();

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.updateEvent(eventDto, EVENT_ID));

            assertEquals("Event with id", exception.getMessage());
        }

        @Test
        @DisplayName("Updating a non-existent event throws a DataValidationException.")
        void testUpdateNonExistingEvent() {
            when(eventRepository.existsById(NON_EXISTING_EVENT_ID)).thenReturn(false);

            assertThrows(DataValidationException.class,
                    () -> eventService.updateEvent(EventDtoBuilder.createValidEventDto(NON_EXISTING_EVENT_ID), NON_EXISTING_EVENT_ID)
            );
            verify(eventRepository, never()).getReferenceById(NON_EXISTING_EVENT_ID);
        }
    }

    @Nested
    @DisplayName("Delete Event Tests")
    class DeleteEventTests {

        @Test
        @DisplayName("Deleting an existing event causes a deletion via the repository.")
        void testDeleteExistingEvent() {
            when(eventRepository.existsById(EVENT_ID)).thenReturn(true);

            eventService.deleteEvent(EVENT_ID);

            verify(eventRepository).existsById(EVENT_ID);
            verify(eventRepository).deleteById(EVENT_ID);
        }

        @Test
        @DisplayName("Deleting a non-existent event throws a DataValidationException.")
        void testDeleteNonExistingEvent() {
            when(eventRepository.existsById(NON_EXISTING_EVENT_ID)).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.deleteEvent(NON_EXISTING_EVENT_ID)
            );

            assertEquals("Event with id", exception.getMessage());
            verify(eventRepository, never()).deleteById(NON_EXISTING_EVENT_ID);
        }
    }

    @Nested
    @DisplayName("Get Event Tests")
    class GetEventTests {

        @Test
        @DisplayName("Getting an event by an existing ID returns the full DTO.")
        void testGetEventExistingId() {
            when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(EventDtoBuilder.createValidEventDto(EVENT_ID));

            EventDto result = eventService.getEvent(EVENT_ID);

            assertAll(
                    () -> assertEquals(EVENT_ID, result.getId()),
                    () -> assertEquals("Moscow", result.getLocation()),
                    () -> assertEquals(EventStatus.PLANNED, result.getEventStatus())
            );
            verify(eventMapper, times(1)).toDto(event);
        }

        @Test
        @DisplayName("Getting an event by a non-existent ID throws a DataValidationException.")
        void testGetEventNonExistingId() {
            when(eventRepository.findById(NON_EXISTING_EVENT_ID)).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.getEvent(NON_EXISTING_EVENT_ID)
            );

            assertEquals("Event with id", exception.getMessage());
        }
    }

    @ParameterizedTest(name = "Filter: title={0}, ownerId={1}, startDate={2}, endDate={3} => expected {4} events")
    @MethodSource("filterTestCases")
    @DisplayName("Filtering events by different criteria returns the correct number of results.")
    void testGetEventsByFilter(
            String title, Long ownerId, LocalDateTime startDate, LocalDateTime endDate, int expectedCount
    ) {
        List<Event> events = List.of(
                EventBuilder.createValidEvent(1L, validUser),
                EventBuilder.createValidEvent(2L, validUser),
                EventBuilder.createValidEvent(3L, validUser)
        );
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDto> result = eventService.getEventsByFilter(
                new EventFilterDto(title, startDate, endDate, ownerId)
        );

        assertEquals(expectedCount, result.size());
    }

    private static Stream<Object[]> filterTestCases() {
        return Stream.of(
                new Object[]{"Presentation", 1L, null, null, 3},
                new Object[]{"Conference", 1L, null, null, 0},
                new Object[]{null, null, null, null, 3},
                new Object[]{null, 1L, BASELINE_TIME.plusHours(1), BASELINE_TIME.plusDays(3), 3}
        );
    }

    @Test
    @DisplayName("Date check: the DataValidationException is thrown out, if the end date is after start date.")
    void testValidateDatesWhenEndBeforeStart() {
        EventDto invalidDto = EventDto
                .builder()
                .startDate(BASELINE_TIME.plusDays(2))
                .endDate(BASELINE_TIME.plusDays(1))
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.create(invalidDto)
        );

        assertEquals("User not found", exception.getMessage());
    }
}