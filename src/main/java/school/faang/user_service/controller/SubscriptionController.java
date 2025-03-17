package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.subscription.FollowRequestDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody FollowRequestDto followRequestDto) throws JsonProcessingException {
        subscriptionService.followUser(followRequestDto);
        return ResponseEntity.ok("User followed successfully");
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody FollowRequestDto followRequestDto) {
        subscriptionService.unfollowUser(followRequestDto);
        return ResponseEntity.ok("User unfollowed successfully");
    }

    @GetMapping("/followers/{userId}")
    public List<UserDto> getFollowers(@PathVariable long userId,
                                      @RequestParam(required = false) UserFilterDto filters) {
        return subscriptionService.getFollowers(userId, filters);
    }

    @GetMapping("/followers-number/{userId}")
    public int getFollowersCount(@PathVariable long userId) {
        return subscriptionService.getFollowersCount(userId);
    }

    @GetMapping("/following/{userId}")
    public List<UserDto> getFollowing(@PathVariable long userId,
                                      @RequestParam(required = false) UserFilterDto filters) {
        return subscriptionService.getFollowing(userId, filters);
    }

    @GetMapping("/following-number/{userId}")
    public int getFollowingCount(@PathVariable long userId) {
        return subscriptionService.getFollowingCount(userId);
    }
}
