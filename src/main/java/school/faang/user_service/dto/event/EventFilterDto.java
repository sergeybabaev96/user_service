package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long ownerId;
    private List<Long> relatedSkills;
    private String location;
    private EventType eventType;
    private EventStatus eventStatus;
}
