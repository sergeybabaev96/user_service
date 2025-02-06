package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventUpdateDto {
    private Long id;
    private Long ownerEventId;
    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillIds;
    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
}
