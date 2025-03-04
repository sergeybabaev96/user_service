package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserFilterMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserFilterMapper userFilterMapper;
    private final UserMapper userMapper;

    public void followUser(long followerId, long targetId) {
        subscriptionService.followUser(followerId, targetId);
    }

    public List<UserDto> getFollowers(long id, UserFilterDto filter) {
        return subscriptionService.getFollowers(id, userFilterMapper.toEntity(filter))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowersCount(long id) {
        return subscriptionService.getFollowersCount(id);
    }

    public long getFollowingCount(long id) {
        return subscriptionService.getFollowingCount(id);
    }
}