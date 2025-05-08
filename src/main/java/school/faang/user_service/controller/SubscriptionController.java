package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followeeId}")
    public void followUser(@PathVariable("followerId") long followerId, @PathVariable("followeeId") long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant subscribe to yourself");
        }

        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public void unfollowUser(@PathVariable("followerId") long followerId, @PathVariable("followeeId") long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant unsubscribe to yourself");
        }

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable("followeeId") long followeeId,
                                      @ModelAttribute UserFilterDto filterDto) {
        return subscriptionService.getFollowers(followeeId, filterDto);
    }

    @GetMapping("{followeeId}/followersCount")
    public Integer getFollowersCount(@PathVariable("followeeId") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("{followeeId}/following")
    public List<UserDto> getFollowing(@PathVariable("followeeId") long followeeId,
                                      @ModelAttribute UserFilterDto filterDto) {
        return subscriptionService.getFollowing(followeeId, filterDto);
    }

    @GetMapping("{followerId}/followingCount")
    public Integer getFollowingCount(@PathVariable("followerId") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    private boolean checkFollowForSelf(long followerId, long followeeId) {
        return followerId == followeeId;
    }
}
