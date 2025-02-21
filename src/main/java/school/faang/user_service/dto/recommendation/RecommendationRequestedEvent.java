package school.faang.user_service.dto.recommendation;

import lombok.Builder;

@Builder
public record RecommendationRequestedEvent(
        long requestAuthorId,
        long targetUserId,
        long recommendationRequestId
) {}
