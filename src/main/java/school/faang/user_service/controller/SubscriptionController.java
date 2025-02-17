package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    private void follow(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    private void unfollow(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    private List<SubscriptionUserDto> getFollowers(long followeeId, UserFilterDto dto) {
        return subscriptionService.getFollowers(followeeId, dto);
    }

    private int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    private List<SubscriptionUserDto> getFollowing(long followerId, UserFilterDto dto) {
        return subscriptionService.getFollowing(followerId, dto);
    }

    private int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
