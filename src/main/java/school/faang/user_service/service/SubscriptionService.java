package school.faang.user_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.subscription.FollowRequestDto;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserPageFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(FollowRequestDto followRequestDto) throws JsonProcessingException {
        long followerId = followRequestDto.getFollowerId();
        long followeeId = followRequestDto.getFolloweeId();
        if (followerId == followeeId) {
            throw new DataValidationException("You can`t follow yourself");
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already following this user");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        followerEventPublisher.publish(new FollowerEvent(followerId, followeeId));
    }

    @Transactional
    public void unfollowUser(FollowRequestDto followRequestDto) {
        long followerId = followRequestDto.getFollowerId();
        long followeeId = followRequestDto.getFolloweeId();
        if (followerId == followeeId) {
            throw new DataValidationException("You can`t unfollow yourself");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long userId, UserFilterDto filters) {
        Stream<User> userStream = subscriptionRepository.findFollowersByUserId(userId);

        return filterUsersAndGetDtoList(filters, userStream);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long userId) {
        return subscriptionRepository.findFollowersAmountByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long userId, UserFilterDto filters) {
        Stream<User> userStream = subscriptionRepository.findFolloweesByUserId(userId);

        return filterUsersAndGetDtoList(filters, userStream);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long userId) {
        return subscriptionRepository.findFolloweesAmountByUserId(userId);
    }

    private List<UserDto> filterUsersAndGetDtoList(UserFilterDto filters, Stream<User> userStream) {
        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filters)) {
                userStream = userFilter.apply(userStream, filters);
            }
        }

        UserPageFilter userPageFilter = new UserPageFilter();
        if (userPageFilter.isApplicable(filters)) {
            userStream = userPageFilter.apply(userStream, filters);
        }

        return userStream
                .map(userMapper::toDto)
                .toList();
    }
}
