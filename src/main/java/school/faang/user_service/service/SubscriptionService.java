package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.UserFollowersFilter;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFollowersFilter> followersFilter;
    private final UserMapper userMapper;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} attempted to follow a user {} they already follow", followerId, followeeId);
            throw new DataValidationException("You are already followed this account!");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User {} successfully followed user {}", followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} attempted to unfollow a user {} they do not follow", followerId, followeeId);
            throw new DataValidationException("You are not following this user!");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User {} successfully unfollowed user {}", followerId, followeeId);
    }

    @Transactional
    public List<UserDto> getFollowers(Long followeeId, UserFilterDto filter) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);
        if (filter == null) {
            return userStream
                    .map(userMapper::toDto)
                    .toList();
        }
        return userStream
                .filter(follower ->
                        followersFilter.stream()
                                .allMatch(f -> f.apply(follower, filter)))
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public List<UserDto> getFollowing(Long followeeId, UserFilterDto filter) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);
        if (filter == null) {
            return userStream
                    .map(userMapper::toDto)
                    .toList();
        }
        return userStream
                .filter(follower ->
                        followersFilter.stream()
                                .allMatch(f -> f.apply(follower, filter)))
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public int getFollowingCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }
}
