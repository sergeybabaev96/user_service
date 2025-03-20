package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.databuilder.event.EventBuilder;
import school.faang.user_service.databuilder.event.EventDtoBuilder;
import school.faang.user_service.databuilder.event.SkillBuilder;
import school.faang.user_service.databuilder.event.UserBuilder;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventMapper Tests")
class EventMapperTest {

    @Mock
    private SkillRepository skillRepository;

    private final EventMapper mapper = Mappers.getMapper(EventMapper.class);
    private User testOwner;

    private static final long EVENT_ID = 1L;
    private static final long SKILL_JAVA_ID = 10L;

    @BeforeEach
    @DisplayName("Setup test owner")
    void setUp() {
        testOwner = User.builder().id(1L).build();
    }

    @Nested
    @DisplayName("Event to EventDto Mapping")
    class EventToDtoTests {

        @Test
        @DisplayName("Mapping entity to DTO works correctly")
        void testMapEntityToDto() {
            Event event = EventBuilder.createValidEvent(1L, testOwner);
            event.setAttendees(List.of(
                    UserBuilder.createValidUser(1L),
                    UserBuilder.createValidUser(2L))
            );
            event.setMaxAttendees(100);

            EventDto dto = mapper.toDto(event);

            System.out.println("RelatedSkills: " + dto.getRelatedSkills());
            System.out.println("Event related skills: " + event.getRelatedSkills());

            assertAll(
                    () -> assertEquals(event.getId(), dto.getId()),
                    () -> assertEquals(event.getTitle(), dto.getTitle()),
                    () -> assertEquals(event.getDescription(), dto.getDescription()),
                    () -> assertEquals(event.getLocation(), dto.getLocation()),
                    () -> assertEquals(event.getStartDate(), dto.getStartDate()),
                    () -> assertEquals(event.getEndDate(), dto.getEndDate()),
                    () -> assertEquals(event.getType(), dto.getEventType()),
                    () -> assertEquals(event.getStatus(), dto.getEventStatus()),
                    () -> assertEquals(event.getMaxAttendees(), dto.getMaxAttendees()),
                    () -> assertEquals(2, event.getAttendees().size()),
                    () -> assertNotNull(dto.getRelatedSkills(), "RelatedSkills cannot be null")
            );
        }

        @Test
        @DisplayName("Mapping null entity returns null DTO")
        void testMapEntityToDtoWithNullEntity() {
            assertNull(mapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("EventDto to Event Mapping")
    class DtoToEventTests {

        @Test
        @DisplayName("Mapping DTO to entity works correctly")
        void testMapDtoToEntity() {
            EventDto dto = EventDtoBuilder.createValidEventDto(EVENT_ID);
            when(skillRepository.findAllById(any())).thenReturn(List.of(SkillBuilder.createValidSkill(SKILL_JAVA_ID, "Java")));

            Event event = mapper.toEntity(dto, skillRepository);

            assertAll(
                    () -> assertEquals(dto.getId(), event.getId()),
                    () -> assertEquals(dto.getTitle(), event.getTitle()),
                    () -> assertEquals(dto.getDescription(), event.getDescription()),
                    () -> assertEquals(dto.getLocation(), event.getLocation()),
                    () -> assertEquals(dto.getStartDate(), event.getStartDate()),
                    () -> assertEquals(dto.getEndDate(), event.getEndDate()),
                    () -> assertEquals(dto.getEventType(), event.getType()),
                    () -> assertEquals(dto.getEventStatus(), event.getStatus()),
                    () -> assertEquals(dto.getMaxAttendees(), event.getMaxAttendees()),
                    () -> assertEquals(1L, event.getOwner().getId()),
                    () -> assertEquals(1, event.getRelatedSkills().size())
            );
        }

        @Test
        @DisplayName("Mapping null DTO returns null entity")
        void testMapDtoToEntityWithNullDto() {
            assertNull(mapper.toEntity(null, skillRepository));
        }
    }

    @Nested
    @DisplayName("Updating an Event with DTO")
    class UpdateEventTests {

        @Test
        @DisplayName("Updating an entity with non-null DTO updates the entity correctly")
        void testUpdateEntityWithNotNullDto() {
            Event existing = EventBuilder.createValidEvent(EVENT_ID, testOwner);
            EventType originalType = existing.getType();
            EventDto update = EventDto.builder()
                    .title("Updated Title")
                    .description("New Description")
                    .maxAttendees(50)
                    .build();

            mapper.updateEventFormDto(update, existing, skillRepository);

            assertAll(
                    () -> assertEquals("Updated Title", existing.getTitle()),
                    () -> assertEquals("New Description", existing.getDescription()),
                    () -> assertEquals(50, existing.getMaxAttendees()),
                    () -> assertEquals(originalType, existing.getType())
            );
        }

        @Test
        @DisplayName("Updating an entity with a null DTO leaves the entity unchanged")
        void testUpdateEntityWithNullDto() {
            Event original = EventBuilder.createValidEvent(EVENT_ID, testOwner);
            String originalTitle = original.getTitle();
            int originalMaxAttendees = original.getMaxAttendees();
            EventType originalType = original.getType();
            String originalLocation = original.getLocation();

            EventDto update = EventDto.builder()
                    .title("Java presentation")
                    .description("Updated Description")
                    .maxAttendees(100)
                    .eventType(EventType.PRESENTATION)
                    .location("Moscow")
                    .build();

            mapper.updateEventFormDto(update, original, skillRepository);

            assertAll(
                    () -> assertEquals(originalTitle, original.getTitle(), "Title should not be updated"),
                    () -> assertEquals("Updated Description", original.getDescription(), "Description should be updated"),
                    () -> assertEquals(originalMaxAttendees, original.getMaxAttendees(), "MaxAttendees should not be updated"),
                    () -> assertEquals(originalType, original.getType(), "EventType should not be updated"),
                    () -> assertEquals(originalLocation, original.getLocation(), "Location should not be updated")
            );
        }
    }
}