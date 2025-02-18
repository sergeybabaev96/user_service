package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @PostMapping
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/getFollowers")
    public List<UserDto> getFollowers(@RequestParam long followerId, @RequestBody UserFilterDto filterDto) {
        List<User> users = subscriptionService.getFollowers(followerId, filterDto);
        return userMapper.toDtoList(users);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        List<User> users = subscriptionService.getFollowing(followeeId, filterDto);
        return userMapper.toDtoList(users);
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}

