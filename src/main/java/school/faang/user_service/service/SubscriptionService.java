package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.subscriber.SubscriberFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final List<SubscriberFilter> subscriberFilters;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        validateUserExists(followeeId);
        validateUserExists(followerId);
        validateSubscriptionOnYourself(followerId, followeeId,
                ErrorMessage.SUBSCRIBING_ON_YOURSELF_ERROR_MSG);
        validateSubscription(followerId, followeeId, true);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateUserExists(followeeId);
        validateUserExists(followerId);
        validateSubscriptionOnYourself(followerId, followeeId,
                ErrorMessage.UNSUBSCRIBING_FROM_YOURSELF_ERROR_MSG);
        validateSubscription(followerId, followeeId, false);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        validateUserExists(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(filters, followers);
    }

    public int getFollowersCount(long followeeId) {
        validateUserExists(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        validateUserExists(followerId);
        Stream<User> following = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(filters, following);
    }

    public int getFollowingCount(long followerId) {
        validateUserExists(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateSubscriptionOnYourself(long followerId, long followeeId, ErrorMessage errorMessage) {
        if (followerId == followeeId) {
            log.error(errorMessage.getMessage());
            throw new DataValidationException(errorMessage.getMessage());
        }
    }

    private void validateSubscription(long followerId, long followeeId, boolean isFollowAction) {
        boolean isSubscribed = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isFollowAction && isSubscribed) {
            log.error(ErrorMessage.ALREADY_SUBSCRIBED_ERROR_MSG.getMessage());
            throw new DataValidationException(ErrorMessage.ALREADY_SUBSCRIBED_ERROR_MSG.getMessage());
        }
        if (!isFollowAction && !isSubscribed) {
            log.error(ErrorMessage.IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG.getMessage());
            throw new DataValidationException(ErrorMessage.IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG.getMessage());
        }
    }

    private void validateUserExists(long userId) {
        if (!subscriptionRepository.existsById(userId)) {
            log.error(ErrorMessage.USER_DOES_NOT_EXIST_BY_ID_ERROR_MSG.getMessage(), userId);
            throw new DataValidationException(ErrorMessage.USER_DOES_NOT_EXIST.getMessage());
        }
    }

    private List<UserDto> filterUsers(UserFilterDto filters, Stream<User> users) {
        List<SubscriberFilter> applicableFilters = getApplicableFilters(filters);

        if (applicableFilters.isEmpty()) {
            return Collections.emptyList();
        }

        Stream<User> filteredUsers = applyFilters(applicableFilters, filters, users);
        return userMapper.toDtoList(filteredUsers.toList());
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
