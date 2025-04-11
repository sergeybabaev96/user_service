package school.faang.user_service.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription Management", description = "Provides methods for managing user subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Follow user", description = "Subscribe one user to another")
    @PostMapping
    public ResponseEntity<Void> followUser(
            @RequestParam @Min(1) long followerId,
            @RequestParam @Min(1) long followeeId) {

        log.info("Follow request: followerId={}, followeeId={}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unfollow user", description = "Unsubscribe one user from another")
    @DeleteMapping
    public ResponseEntity<Void> unfollowUser(
            @RequestParam @Min(1) long followerId,
            @RequestParam @Min(1) long followeeId) {

        log.info("Unfollow request: followerId={}, followeeId={}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get followers", description = "Get list of followers for specified user with optional filtering")
    @GetMapping("/followers/{userId}")
    public List<UserViewDto> getFollowers(
            @PathVariable("userId") @Min(1) long followeeId,
            @Valid @ModelAttribute UserFilterDto filter) {

        log.info("Fetching followers for user ID: {} with filter: {}", followeeId, filter);
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Get followers count", description = "Get number of followers for specified user")
    @GetMapping("/followers/{userId}/count")
    public int getFollowersCount(@PathVariable("userId") @Min(1) long followeeId) {
        log.info("Counting followers for user ID: {}", followeeId);
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Get following", description = "Get list of users that specified user is following with optional filtering")
    @GetMapping("/following/{userId}")
    public List<UserViewDto> getFollowing(
            @PathVariable("userId") @Min(1) long followerId,
            @Valid @ModelAttribute UserFilterDto filter) {

        log.info("Fetching following list for user ID: {} with filter: {}", followerId, filter);
        return subscriptionService.getFollowing(followerId, filter);
    }

    @Operation(summary = "Get following count", description = "Get number of users that specified user is following")
    @GetMapping("/following/{userId}/count")
    public int getFollowingCount(@PathVariable("userId") @Min(1) long followerId) {
        log.info("Counting following for user ID: {}", followerId);
        return subscriptionService.getFollowingCount(followerId);
    }
}