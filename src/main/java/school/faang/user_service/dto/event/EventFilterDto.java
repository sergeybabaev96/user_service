package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDateBefore;
    private LocalDateTime startDateAfter;
    private String description;
    private String location;
    private EventType eventType;
}
