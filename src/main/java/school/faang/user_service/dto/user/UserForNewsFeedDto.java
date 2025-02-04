package school.faang.user_service.dto.user;

import java.util.List;

public record UserForNewsFeedDto (
        Long id,
        String username,
        List<Long> followerIds,
        List<Long> followeeIds
) {
}
