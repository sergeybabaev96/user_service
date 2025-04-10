package school.faang.user_service.dto.pubsub;

import java.time.LocalDateTime;

public record RecommendationReceivedEvent(
        long authorId,
        long receiverId,
        String message,
        LocalDateTime recommendationTime) {
}
