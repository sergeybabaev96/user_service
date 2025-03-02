package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProfileViewEvent {
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
