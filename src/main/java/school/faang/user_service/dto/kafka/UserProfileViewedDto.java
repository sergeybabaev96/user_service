package school.faang.user_service.dto.kafka;

import java.time.LocalDateTime;

public record UserProfileViewedDto(
        Long viewerId,
        Long profileOwnerId,
        LocalDateTime viewedTime
) {}
