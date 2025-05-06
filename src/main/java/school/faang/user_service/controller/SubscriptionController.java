package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.DataValidationException;
import school.faang.user_service.util.SubscriptionErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable("followerId") long followerId,
                                           @PathVariable("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable("followerId") long followerId,
                                             @PathVariable("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/followers/{id}")
    public List<UserDto> getFollowers(@PathVariable("id") long followeeId, UserDtoFilter userDtoFilter) {
        return subscriptionService.getFollowers(followeeId, userDtoFilter);
    }

    @GetMapping("/followers/{id}/count")
    public int getFollowerCount(@PathVariable("id") long followerId) {
        return subscriptionService.getFollowerCount(followerId);
    }

    @GetMapping("/subscription/{id}")
    public List<UserDto> getFollowing(@PathVariable("id") long id, UserDtoFilter userDtoFilter) {
        return subscriptionService.getFollowing(id, userDtoFilter);
    }

    @GetMapping("/subscriptions/{id}/count")
    public int getFollowingCount(@PathVariable("id") long id) {
        return subscriptionService.getFollowingCount(id);
    }

    @ExceptionHandler
    private ResponseEntity<SubscriptionErrorResponse> handleException(DataValidationException e) {
        SubscriptionErrorResponse response = new SubscriptionErrorResponse(e.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
