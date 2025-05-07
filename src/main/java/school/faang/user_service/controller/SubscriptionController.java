package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void follow(long followerId, long followeeId) {
        log.info("Incoming follow request: followerId={} -> followeeId={}", followerId, followeeId);
        validateNotSelf(followerId, followeeId, "follow");
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        log.info("Incoming unfollow request: followerId={} -> followeeId={}", followerId, followeeId);
        validateNotSelf(followerId, followeeId, "unfollow");
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} followers with filter {}", followeeId, filter);
        validatePositiveId(followeeId);
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followeeId) {
        log.info("Received GET {} followers count request", followeeId);
        validatePositiveId(followeeId);
        return subscriptionService.getFollowersCount(followeeId);
    }


    public List<UserDto> getFollowing(Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} following with filter {}", followeeId, filter);
        validatePositiveId(followeeId);
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowingCount(long followeeId) {
        log.info("Received GET {} following count request", followeeId);
        validatePositiveId(followeeId);
        return subscriptionService.getFollowingCount(followeeId);
    }

    private void validateNotSelf(long followerId, long followeeId, String action) {
        if (followeeId == followerId) {
            log.warn("User {} attempted to {} themselves", followeeId, action);
            throw new DataValidationException(String.format("You cannot %s yourself!", action));
        }
    }

    private void validatePositiveId(long userId) {
        if (userId <= 0) {
            log.warn("Invalid userId provided: {}", userId);
            throw new DataValidationException("userId must be a positive number!");
        }
    }
}
