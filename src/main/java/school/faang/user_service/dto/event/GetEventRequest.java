package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class GetEventRequest {
    private EventDto filter;
    private Integer limit;
    private Integer offset;
}
