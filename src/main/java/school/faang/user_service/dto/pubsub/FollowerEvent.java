package school.faang.user_service.dto.pubsub;

import java.time.LocalDateTime;

public record FollowerEvent(long followerId, long followeeId, LocalDateTime subscriptionDate) {
}
