package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record FollowRequestDto(
        long followerId,
        long followeeId) {
}
