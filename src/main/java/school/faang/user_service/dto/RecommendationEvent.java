package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record RecommendationEvent(
        Long requesterId,
        Long receiverId,
        Long recommendationId
) {
}
