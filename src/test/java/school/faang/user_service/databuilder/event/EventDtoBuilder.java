package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@UtilityClass
public class EventDtoBuilder {

    private static final LocalDateTime BASELINE_TIME = LocalDateTime.now().plusYears(1);

    public static EventDto createValidEventDto(Long id) {
        return EventDto.builder()
                .id(id)
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
    }

    public static EventDto createInvalidEventDto() {
        return EventDto.builder()
                .id(null)
                .title("Python presentation")
                .startDate(LocalDateTime.now().minusDays(3))
                .endDate(BASELINE_TIME.plusDays(10))
                .ownerId(1L)
                .description("IT presentation")
                .relatedSkills(List.of(10L, 20L))
                .location("London")
                .maxAttendees(50)
                .eventType(EventType.PRESENTATION)
                .eventStatus(EventStatus.PLANNED)
                .build();
    }
}
