package school.faang.user_service.dto.error;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String message,
        Integer statusCode,
        String statusName
) {
}
