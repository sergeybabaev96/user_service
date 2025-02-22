package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class ProfileViewEvent {
    private Long receiverId;
    private Long actorId;
    private LocalDateTime receivedAt;
}
