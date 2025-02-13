package school.faang.user_service.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Follow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully followed"),
            @ApiResponse(responseCode = "400", description = "Invalid followee or follower ID")
    })
    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't follow itself");

        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(summary = "Unfollow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully unfollowed"),
            @ApiResponse(responseCode = "400", description = "Invalid followee or follower ID")
    })
    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't unfollow itself");

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(summary = "Get filtered followers' information by followee ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers were found"),
            @ApiResponse(responseCode = "400", description = "Invalid followee ID")
    })
    @PostMapping("/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Get count of followers by followee ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid followee ID")
    })
    @GetMapping("/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followings' information found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid followee ID")
    })
    @PostMapping("/followings/{followeeId}")
    public List<UserDto> getFollowing(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followings count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid followee ID")
    })
    @GetMapping("/followings/count/{followeeId}")
    public int getFollowingCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping("/{followeeId}/followers/{followerId}")
    public boolean checkFollowerOfFollowee(@PathVariable long followeeId, @PathVariable long followerId) {
        return subscriptionService.checkFollowerOfFollowee(followeeId, followerId);
    }

    @GetMapping("/{followeeId}")
    public List<Long> getFollowersIds(@PathVariable long followeeId) {
        return subscriptionService.getFollowersIds(followeeId);
    }

    @GetMapping("/ids/{followerId}")
    public List<Long> getFolloweesIds(@PathVariable Long followerId) {
        return subscriptionService.getFolloweesIds(followerId);
    }

    private void isFollowerFolloweeIdsEqual(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}