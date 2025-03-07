package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class EventStartDto {
    @NotEmpty
    private Long eventId;
    private Long ownerId;
    private List<Long> userIds;
    private LocalDateTime startTime;
}
