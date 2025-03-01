package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/{followeeId}")
    public List<UserDto> getFollowees(@PathVariable long followeeId, @Valid @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowees(followeeId, userFilterDto);
    }

    @GetMapping("/followers/{followerId}")
    public List<UserDto> getFollowers(@PathVariable long followerId, @Valid @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followerId, userFilterDto);
    }

    @GetMapping("/{followerId}/follow/{followeeId}")
    public boolean isFollow(@PathVariable long followerId, @PathVariable long followeeId) {
        return subscriptionService.isFollow(followeeId, followerId);
    }

    @GetMapping("/followeesCount")
    public long getFollowingCount(@RequestParam long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping("/followersCount")
    public long getFollowersCount(@RequestParam long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping("/follow")
    public void followerUser(@Valid @RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.followUser(followingFeatureDto);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(@Valid @RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.unfollowUser(followingFeatureDto);
    }
}
