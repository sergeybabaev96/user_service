package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnalyticsProfileViewEvent implements Event {
    private Long userId;
    private Long viewerUserId;
    private LocalDateTime timestamp;
}