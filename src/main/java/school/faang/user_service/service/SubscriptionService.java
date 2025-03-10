package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.SubscriberMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final List<Filter> subscriberFilters;
    private final SubscriberMapper subscriberMapper;

    public void followUser(long followerId, long followeeId) {
        validateSubscriptionOnYourself(followerId, followeeId,
                ServiceLogMessages.SUBSCRIBING_ON_YOURSELF_ERROR_MSG);
        validateSubscription(followerId, followeeId,
                ServiceLogMessages.ALREADY_SUBSCRIBED_ERROR_MSG);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateSubscriptionOnYourself(followerId, followeeId,
                ServiceLogMessages.UNSUBSCRIBING_FROM_YOURSELF_ERROR_MSG);
        validateSubscription(followerId, followeeId,
                ServiceLogMessages.IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followeeId);
        return filterUsers(filters, followers);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> following = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(filters, following);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateSubscriptionOnYourself(long followerId, long followeeId, String errorMessage) {
        if (followerId != followeeId) {
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }

    private void validateSubscription(long followerId, long followeeId, String errorMessage) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }

    private List<UserDto> filterUsers(UserFilterDto filters, Stream<User> users) {
        List<Filter> applicableFilters = getApplicableFilters(filters);

        if (applicableFilters.isEmpty()) {
            return Collections.emptyList();
        }

        Stream<User> filteredUsers = applyFilters(applicableFilters, filters, users);
        return mapUsersToDto(filteredUsers);
    }

    private List<Filter> getApplicableFilters(UserFilterDto filters) {
        return subscriberFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
    }

    private Stream<User> applyFilters(List<Filter> filters, UserFilterDto filterDto, Stream<User> users) {
        Stream<User> result = users;
        for (Filter filter : filters) {
            result = filter.apply(result, filterDto);
        }
        return result;
    }

    private List<UserDto> mapUsersToDto(Stream<User> users) {
        return users.map(subscriberMapper::toDto)
                .toList();
    }
}
