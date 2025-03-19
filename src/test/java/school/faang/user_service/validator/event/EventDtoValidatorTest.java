package school.faang.user_service.validator.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.EventDtoValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventDtoValidator Tests")
class EventDtoValidatorTest {

    private static final LocalDateTime BASELINE_TIME = LocalDateTime.of(2026, 3, 10, 18, 0);

    @Nested
    @DisplayName("Valid Event Validation Tests")
    class ValidEventTests {

        @Test
        @DisplayName("Should not throw exception for valid EventDto")
        void testValidateValidEvent() {
            EventDto validDto = EventDto.builder()
                    .id(1L)
                    .title("Java presentation")
                    .startDate(BASELINE_TIME.plusDays(8))
                    .endDate(BASELINE_TIME.plusDays(10))
                    .ownerId(1L)
                    .description("IT presentation")
                    .relatedSkills(List.of(10L, 20L))
                    .location("Moscow")
                    .maxAttendees(100)
                    .eventType(EventType.PRESENTATION)
                    .eventStatus(EventStatus.PLANNED)
                    .build();

            assertDoesNotThrow(() -> EventDtoValidator.validate(validDto));
        }
    }

    @Nested
    @DisplayName("Null Event Validation Tests")
    class NullEventTests {

        @Test
        @DisplayName("Should throw DataValidationException when EventDto is null")
        void testValidateNullEvent() {
            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(null),
                    "EventDto cannot be null"
            );
        }
    }

    @Nested
    @DisplayName("Title Validation Tests")
    class TitleValidationTests {

        @Test
        @DisplayName("Should throw DataValidationException when title is empty")
        void testValidateEmptyTitle() {
            EventDto dto = EventDto
                    .builder()
                    .title(" ")
                    .build();

            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(dto),
                    "Title cannot be empty"
            );
        }

        @Test
        @DisplayName("Should throw DataValidationException when title is null")
        void testValidateNullTitle() {
            EventDto dto = EventDto
                    .builder()
                    .title(null)
                    .build();

            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(dto),
                    "Title cannot be null"
            );
        }
    }

    @Nested
    @DisplayName("Date Validation Tests")
    class DateValidationTests {

        @Test
        @DisplayName("Should throw DataValidationException when start date is in the past")
        void testValidateStartDateInPast() {
            EventDto dto = EventDto
                    .builder()
                    .startDate(BASELINE_TIME.minusDays(15))
                    .build();

            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(dto),
                    "Start date cannot be in the past"
            );
        }

        @Test
        @DisplayName("Should throw DataValidationException when end date is before start date")
        void testValidateEndDateBeforeStart() {
            EventDto dto = EventDto
                    .builder()
                    .startDate(BASELINE_TIME.plusDays(2))
                    .endDate(BASELINE_TIME.plusDays(1))
                    .build();

            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(dto),
                    "End date must be after start date"
            );
        }
    }

    @Nested
    @DisplayName("Owner Validation Tests")
    class OwnerValidationTests {

        @Test
        @DisplayName("Should throw DataValidationException when owner ID is null")
        void testValidateNullOwnerId() {
            EventDto dto = EventDto
                    .builder()
                    .ownerId(null)
                    .build();

            assertThrows(DataValidationException.class,
                    () -> EventDtoValidator.validate(dto),
                    "Owner ID cannot be null"
            );
        }
    }
}
