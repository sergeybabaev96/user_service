package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;

public record RecommendationReceivedEvent(
        long requesterId,
        long receiverId,
        String recommendationMessage,
        LocalDateTime createdAt) {
}
