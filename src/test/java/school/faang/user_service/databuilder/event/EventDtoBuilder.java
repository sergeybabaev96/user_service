package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@UtilityClass
public class EventDtoBuilder {

    private static final LocalDateTime BASELINE_TIME = LocalDateTime.of(2026, 3, 10, 18, 0);

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
}
