package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class EventStartDto {
    @NotEmpty
    private Long eventId;
    private Long ownerId;
    private List<Long> userIds;
    private LocalDateTime startTime;
    private String message;
}
