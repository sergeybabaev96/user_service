package school.faang.user_service.dto.kafka;

import java.time.LocalDateTime;

public record FollowUserEventDto (
    Long followerId,
    Long followeeId,

    LocalDateTime followedAt
){}