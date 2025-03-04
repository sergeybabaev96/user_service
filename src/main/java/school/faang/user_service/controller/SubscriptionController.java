package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/follow")
    public void followUser(long followerId, long followeeId) {
        if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("ID пользователей должны быть положительными.");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на самого себя.");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping(value = "/unfollow")
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("ID пользователей должны быть положительными.");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться на самого себя.");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping(value = "/get-following")
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        if (followeeId <= 0) {
            throw new DataValidationException("ID Пользователя должен быть положительным");
        }

        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping(value = "/follow-count")
    public int getFollowingCount(long followerId) {
        if (followerId <= 0) {
            throw new DataValidationException("ID Пользователя должен быть положительным");
        }

        return subscriptionService.getFollowingCount(followerId);
    }

    @GetMapping(value = "/get-followers")
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (followeeId <= 0) {
            throw new DataValidationException("ID Пользователя должен быть положительным");
        }

        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping(value = "/followers-count")
    public int getFollowersCount(long followeeId) {
        if (followeeId <= 0) {
            throw new DataValidationException("ID Пользователя должен быть положительным");
        }

        return subscriptionService.getFollowersCount(followeeId);
    }
}