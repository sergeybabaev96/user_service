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

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final List<SubscriberFilter> userFilters;
    private final SubscriberMapper subscriberMapper;

    public void followUser(long followerId, long followeeId) {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя подписаться на свой аккаунт.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            throw new DataValidationException("Вы уже подписаны на этого пользователя.");
        }

        repository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя отписаться от самого себя.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isThereSub) {
            throw new DataValidationException("Невозможно отписаться от пользователя, на которого вы не подписаны.");
        }

        repository.unfollowUser(followerId, followeeId);
    }

    private void checkingActionsOnYourself(long followerId, long followeeId, String errorMessage) {
        if (followerId == followeeId) {
            throw new DataValidationException(errorMessage);
        }
    }

    public List<SubscriberReadDto> getFollowers(long followeeId, SubscriberFilterDto filters) {
        Stream<User> followers = repository.findByFolloweeId(followeeId);

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .flatMap(userFilter -> userFilter.apply(followers, filters))
                .map(subscriberMapper::toDto)
                .toList();
    }

    public int getFollowersCount(long followeeId) {
        return repository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<SubscriberReadDto> getFollowing(long followeeId, SubscriberFilterDto filters) {
        Stream<User> following = repository.findByFolloweeId(followeeId);

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .flatMap(userFilter -> userFilter.apply(following, filters))
                .map(subscriberMapper::toDto)
                .toList();
    }

    public int getFollowingCount(long followerId) {
        return repository.findFolloweesAmountByFollowerId(followerId);
    }
}