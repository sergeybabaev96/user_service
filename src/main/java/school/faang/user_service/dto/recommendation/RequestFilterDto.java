package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;


public record RequestFilterDto(RequestStatus status) {
}
