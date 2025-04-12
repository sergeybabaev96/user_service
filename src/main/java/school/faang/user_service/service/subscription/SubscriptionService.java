package school.faang.user_service.service.subscription;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.event.SubscriptionEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.subscriber.SubscriberFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.subscription.SubscriptionPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final List<SubscriberFilter> subscriberFilters;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateUserExists(followerId);
        validateUserExists(followeeId);
        validateSubscriptionOnYourself(followerId, followeeId, true);
        validateSubscription(followerId, followeeId, true);
        subscriptionRepository.followUser(followerId, followeeId);
        SubscriptionEventDto event = SubscriptionEventDto.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .eventTime(LocalDateTime.now())
                .build();
        redisTemplate.convertAndSend(redisProperties.getChannel().getFollower(), event);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateUserExists(followerId);
        validateUserExists(followeeId);
        validateSubscriptionOnYourself(followerId, followeeId, false);
        validateSubscription(followerId, followeeId, false);
        subscriptionRepository.unfollowUser(followerId, followeeId);
        SubscriptionEventDto event = SubscriptionEventDto.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .eventTime(LocalDateTime.now())
                .build();
        redisTemplate.convertAndSend(redisProperties.getChannel().getUnfollower(), event);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        validateUserExists(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(filters, followers);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        validateUserExists(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        validateUserExists(followerId);
        Stream<User> following = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(filters, following);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        validateUserExists(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateSubscriptionOnYourself(long followerId, long followeeId, boolean isFollow) {
        if (followerId == followeeId) {
            ErrorMessage errorMessage = isFollow
                    ? ErrorMessage.SUBSCRIBING_ON_YOURSELF_ERROR_MSG
                    : ErrorMessage.UNSUBSCRIBING_FROM_YOURSELF_ERROR_MSG;
            log.error(errorMessage.getMessage());
            throw new DataValidationException(errorMessage.getMessage());
        }
    }

    private void validateSubscription(long followerId, long followeeId, boolean isFollow) {
        boolean isSubscribed = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isFollow && isSubscribed) {
            log.error(ErrorMessage.ALREADY_SUBSCRIBED_ERROR_MSG.getMessage());
            throw new DataValidationException(ErrorMessage.ALREADY_SUBSCRIBED_ERROR_MSG.getMessage());
        }
        if (!isFollow && !isSubscribed) {
            log.error(ErrorMessage.IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG.getMessage());
            throw new DataValidationException(ErrorMessage.IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG.getMessage());
        }
    }

    private void validateUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error(ErrorMessage.USER_DOES_NOT_EXIST_BY_ID_ERROR_MSG.getMessage(), userId);
            throw new DataValidationException(ErrorMessage.USER_DOES_NOT_EXIST.getMessage());
        }
    }

    private SubscriptionEventDto createEvent(long followerId, long followeeId, LocalDateTime eventTime) {
        return SubscriptionEventDto.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .eventTime(eventTime)
                .build();
    }

    private List<UserDto> filterUsers(UserFilterDto filters, Stream<User> users) {
        List<SubscriberFilter> applicableFilters = getApplicableFilters(filters);

        if (applicableFilters.isEmpty()) {
            return Collections.emptyList();
        }

        Stream<User> filteredUsers = applyFilters(applicableFilters, filters, users);
        return userMapper.toListUserDto(filteredUsers.toList());
    }

    private List<SubscriberFilter> getApplicableFilters(UserFilterDto filters) {
        return subscriberFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
    }

    private Stream<User> applyFilters(List<SubscriberFilter> filters, UserFilterDto filterDto, Stream<User> users) {
        Stream<User> result = users;
        for (SubscriberFilter filter : filters) {
            result = filter.apply(result, filterDto);
        }
        return result;
    }
}