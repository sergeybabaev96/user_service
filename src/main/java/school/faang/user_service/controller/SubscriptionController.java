package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/following/{followeeId}")
    public void follow(@PathVariable
                       @NotNull(message = "Follower ID must be provided")
                       @Positive(message = "Follower ID must be positive number")
                       Long followerId,
                       @PathVariable
                       @NotNull(message = "Followee ID must be provided")
                       @Positive(message = "Followee ID must be positive number")
                       Long followeeId) {
        log.info("Incoming follow request: followerId={} -> followeeId={}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/following/{followeeId}")
    public void unfollowUser(@PathVariable
                             @NotNull(message = "Follower ID must be provided")
                             @Positive(message = "Follower ID must be positive number")
                             Long followerId,
                             @PathVariable
                             @NotNull(message = "Followee ID must be provided")
                             @Positive(message = "Followee ID must be positive number")
                             Long followeeId) {
        log.info("Incoming unfollow request: followerId={} -> followeeId={}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable
                                      @NotNull(message = "Followee ID must be provided")
                                      @Positive(message = "Followee ID must be positive number")
                                      Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} followers with filter {}", followeeId, filter);
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/{followeeId}/followers/count")
    public int getFollowersCount(@PathVariable
                                 @NotNull(message = "Followee ID must be provided")
                                 @Positive(message = "Followee ID must be positive number")
                                 Long followeeId) {
        log.info("Received GET {} followers count request", followeeId);
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followeeId}/following")
    public List<UserDto> getFollowing(@PathVariable
                                      @NotNull(message = "Followee ID must be provided")
                                      @Positive(message = "Followee ID must be positive number")
                                      Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} following with filter {}", followeeId, filter);
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/{followeeId}/following/count")
    public int getFollowingCount(@PathVariable
                                 @NotNull(message = "Followee ID must be provided")
                                 @Positive(message = "Followee ID must be positive number")
                                 Long followeeId) {
        log.info("Received GET {} following count request", followeeId);
        return subscriptionService.getFollowingCount(followeeId);
    }
}
