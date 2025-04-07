package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long targetId);

    void unfollowUser(long followerId, long targetId);

    List<UserDto> getFollowers(long id, UserFilterDto filterDto);

    List<UserDto> getFollowing(long id, UserFilterDto filterDto);

    long getFollowersCount(long id);

    long getFollowingCount(long id);
}
