package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;


public record EventFilterDto(
        String title,
        LocalDateTime startDateBefore,
        LocalDateTime startDateAfter,
        String description,
        String location,
        EventType eventType
) {
}
