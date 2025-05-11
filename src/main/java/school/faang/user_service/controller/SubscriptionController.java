package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        try {
            subscriptionService.unfollowUser(followerId, followeeId);
        } catch (IllegalArgumentException error) {
            throw new DataValidationException("You can't unsubscribe to yourself");
        }
    }

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(
            @PathVariable long followeeId,
            @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following/{followeeId}")
    public List<UserDto> getFollowing(
            @PathVariable long followeeId,
            @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/following/count/{followeeId}")
    public int getFollowingCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}
