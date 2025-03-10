package school.faang.user_service.dto.recomendation;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record RequestFilterDto(

        RequestStatus status,

        Long requesterId,

        Long receiverId
) {
}
