package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {
    private Long requestIdPattern;
    private Long receiverIdPattern;
    private String messagePattern;
    private RequestStatus statusPattern;
}
