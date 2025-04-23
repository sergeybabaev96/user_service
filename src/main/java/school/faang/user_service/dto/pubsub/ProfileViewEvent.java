package school.faang.user_service.dto.pubsub;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileViewEvent(
        Long profileId,
        Long viewerId,
        LocalDateTime viewedAt
) {
}
