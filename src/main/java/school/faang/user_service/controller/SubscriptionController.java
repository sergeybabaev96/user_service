package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserContext userContext;

    @PostMapping
    private void follow(@RequestParam long followeeId) {
        subscriptionService.followUser(userContext.getUserId(), followeeId);
    }

    @DeleteMapping
    private void unfollow(@RequestParam long followeeId) {
        subscriptionService.unfollowUser(userContext.getUserId(), followeeId);
    }

    @GetMapping("/{followeeId}/followers")
    private List<SubscriptionUserDto> getFollowers(UserFilterDto dto, @PathVariable long followeeId) {
        return subscriptionService.getFollowers(followeeId, dto);
    }

    @GetMapping("/{followeeId}/followers/count")
    private int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/following")
    private List<SubscriptionUserDto> getFollowing(UserFilterDto dto, @PathVariable long followerId) {
        return subscriptionService.getFollowing(followerId, dto);
    }

    @GetMapping("/{followerId}/following/count")
    private int getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
