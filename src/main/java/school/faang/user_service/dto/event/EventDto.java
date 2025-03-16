package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventDto {
    private final Long id;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime  endDate;
    private final Long ownerId;
    private final String description;
    private final List<Long> relatedSkills;
    private final String location;
    private final int maxAttendees;
    private final EventType eventType;
    private final EventStatus eventStatus;
}
