package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.subscriber.SubscriberReadDto;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;

    @PostMapping("/{followerId}/{followeeId}")
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        service.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/{followeeId}")
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        service.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/{followeeId}")
    public List<SubscriberReadDto> getFollowers(@PathVariable long followeeId,
                                                @ModelAttribute SubscriberFilterDto filters) {
        return service.getFollowers(followeeId, filters);
    }

    @GetMapping("/followers/{followeeId}/count")
    public int getFollowersCount(@PathVariable long followeeId) {
        return service.getFollowersCount(followeeId);
    }

    @GetMapping("/following/{followerId}")
    public List<SubscriberReadDto> getFollowing(@PathVariable long followerId,
                                                @ModelAttribute SubscriberFilterDto filters) {
        return service.getFollowing(followerId, filters);
    }

    @GetMapping("/following/{followerId}/count")
    public int getFollowingCount(@PathVariable long followerId) {
        return service.getFollowingCount(followerId);
    }
}