package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на себя.");
        } else {
            subscriptionService.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться от себя.");
        } else {
            subscriptionService.unfollowUser(followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

}
