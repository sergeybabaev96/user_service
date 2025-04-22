package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.properties.EventType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipAcceptedEventDto {
    private long requestId;
    private long requesterId;
    private long receiverId;
    private EventType eventType;
}
