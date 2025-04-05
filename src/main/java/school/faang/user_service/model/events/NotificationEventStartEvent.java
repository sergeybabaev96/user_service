package school.faang.user_service.model.events;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationEventStartEvent {
    @NotEmpty
    private Long eventId;
    private Long ownerId;
    private List<Long> userIds;
    private LocalDateTime startTime;
    private String message;
}
