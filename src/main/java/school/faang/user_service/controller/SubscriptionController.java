package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/follow")
    public void followUser(@NotNull @RequestParam Long followerId, @NotNull @RequestParam Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping(value = "/unfollow")
    public void unfollowUser(@NotNull @RequestParam Long followerId, @NotNull @RequestParam Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping(value = "/followers/my")
    public List<UserDto> getFollowing(@NotNull @RequestParam Long followeeId, @RequestParam UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping(value = "/followers/my/count")
    public int getFollowingCount(@NotNull @RequestParam Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    @GetMapping(value = "/followers/users/{id}")
    public List<UserDto> getFollowers(@NotNull @PathVariable("id") Long id, @NotNull @RequestParam UserFilterDto filter) {
        return subscriptionService.getFollowers(id, filter);
    }

    @GetMapping(value = "/followers/users/{id}/count")
    public int getFollowersCount(@NotNull @PathVariable Long id) {
        return subscriptionService.getFollowersCount(id);
    }
}