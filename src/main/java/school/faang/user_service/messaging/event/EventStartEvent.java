package school.faang.user_service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
public class EventStartEvent {

    private String eventId;
    private List<UserDto> participants;
}
