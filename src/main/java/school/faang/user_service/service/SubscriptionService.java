package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscriber.SubscriberReadDto;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.subscriber.SubscriberFilter;
import school.faang.user_service.mapper.SubscriberMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final List<SubscriberFilter> subscriberFilters;
    private final SubscriberMapper mapper;

    public void followUser(long followerId, long followeeId) {
        checkActionOnYourself(followerId, followeeId, "Нельзя подписаться на свой аккаунт.");
        checkSubscription(followerId, followeeId, "Вы уже подписаны на этого пользователя.");
        repository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        checkActionOnYourself(followerId, followeeId, "Нельзя отписаться от самого себя.");
        checkSubscription(followerId, followeeId,
                "Невозможно отписаться от пользователя, на которого вы не подписаны.");
        repository.unfollowUser(followerId, followeeId);
    }

    public List<SubscriberReadDto> getFollowers(long followeeId, SubscriberFilterDto filters) {
        Stream<User> followers = repository.findByFolloweeId(followeeId);
        return filterUsers(filters, followers);
    }

    public int getFollowersCount(long followeeId) {
        return repository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<SubscriberReadDto> getFollowing(long followerId, SubscriberFilterDto filters) {
        Stream<User> following = repository.findByFollowerId(followerId);
        return filterUsers(filters, following);
    }

    public int getFollowingCount(long followerId) {
        return repository.findFolloweesAmountByFollowerId(followerId);
    }

    private void checkSubscription(long followerId, long followeeId, String message) {
        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            throw new DataValidationException(message);
        }
    }

    private void checkActionOnYourself(long followerId, long followeeId, String errorMessage) {
        if (followerId == followeeId) {
            throw new DataValidationException(errorMessage);
        }
    }

    private List<SubscriberReadDto> filterUsers(SubscriberFilterDto filters, Stream<User> users) {
        List<SubscriberFilter> applicableFilters = subscriberFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        if (applicableFilters.isEmpty()) {
            return Collections.emptyList();
        }

        return applicableFilters.stream()
                .reduce(users, (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s2)
                .map(mapper::toDto)
                .toList();
    }
}