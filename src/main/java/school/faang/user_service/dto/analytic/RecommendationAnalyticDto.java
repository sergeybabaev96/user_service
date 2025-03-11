package school.faang.user_service.dto.analytic;

import java.time.LocalDateTime;


public record RecommendationAnalyticDto(
        Long recommendationId,
        Long authorId,
        Long receivedId,
        LocalDateTime receivedAt
) {

}
