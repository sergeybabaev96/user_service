package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        if(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("the user is trying to subscribe to himself");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("You can't unsubscribe from yourself");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return filterUser(subscriptionRepository.findByFolloweeId(followeeId).toList(), filter)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<User> filterUser(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> user.getPhone().contains(filter.getPhonePattern()))
                .filter(user -> user.getExperience() < filter.getExperienceMax() && user.getExperience() > filter.getExperienceMin())
                .toList();
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return filterUser(subscriptionRepository.findByFolloweeId(followeeId).toList(), filter)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
