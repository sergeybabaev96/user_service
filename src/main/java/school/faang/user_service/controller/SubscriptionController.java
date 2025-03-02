package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.UserFilter;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long targetId) {
        subscriptionService.followUser(followerId, targetId);
    }

    public List<UserDto> getFollowers(long targetId, UserFilterDto filter) {
        return subscriptionService.getFollowers(targetId, new UserFilter(
                        filter.namePattern()
                        , filter.phonePattern()
                        , filter.experienceMin()
                        , filter.experienceMax()))
                .stream()
                .map(user -> new UserDto(
                        user.getId()
                        , user.getUsername()
                        , user.getEmail()))
                .toList();
    }
}
