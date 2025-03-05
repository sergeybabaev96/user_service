package school.faang.user_service.events;

import lombok.Builder;

@Builder
public record RecommendationEvent(
        Long requesterId,
        Long receiverId,
        Long recommendationId
) {
}
