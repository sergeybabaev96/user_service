package school.faang.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static java.lang.System.lineSeparator;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(long followerId, long followeeId, HttpServletRequest servletRequest) {

        log.info("Recieved HTTP request [POST] {} with parameters {} followerId = {}, followeeId = {}",
                servletRequest.getRequestURL().toString(),
                lineSeparator(),
                followerId,
                followeeId);

        if (followerId == followeeId) {
            log.error("User id={} cannot be follower of himself!", followerId);
            throw new DataValidationException("User id=" + followerId + " cannot be follower of himself!");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(long followerId, long followeeId, HttpServletRequest servletRequest) {
        log.info("Recieved HTTP request [POST] {} with parameters {} followerId = {}, followeeId = {}",
                servletRequest.getRequestURL().toString(),
                lineSeparator(),
                followerId,
                followeeId);
        if (followerId == followeeId) {
            log.error("User id={} cannot to unfollow of himself!", followerId);
            throw new DataValidationException("User id=" + followerId + " cannot to unfollow of himself!");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/following")
    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public RecordsQuantityDto getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/following/count")
    public RecordsQuantityDto getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
