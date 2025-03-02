package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long targetId) {
        ensureSubscriptionStateValidation(followerId, targetId, false);
        subscriptionRepository.followUser(followerId, targetId);
    }

    public void unfollowUser(long followerId, long targetId) {
        ensureSubscriptionStateValidation(followerId, targetId, true);
        subscriptionRepository.unfollowUser(followerId, targetId);
    }

    private void ensureSubscriptionStateValidation(long followerId, long targetId, boolean shouldExist) {
        if (followerId == targetId)
            throw new DataValidationException("A user cannot follow themselves. UserId: " + targetId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, targetId) != shouldExist)
            throw new DataValidationException("The subscription has already been issued");
    }

    public List<User> getFollowers(long id) {
        return subscriptionRepository.findByFolloweeId(id).toList();
    }

    public List<User> getFollowers(long id, Predicate<User> filter) {
        return subscriptionRepository.findByFolloweeId(id).filter(filter).toList();
    }

    public long getFollowersCount(long id) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(id);
    }

    public List<User> getFollowing(long id) {
        return subscriptionRepository.findByFollowerId(id).toList();
    }

    public List<User> getFollowing(long id, Predicate<User> filter) {
        return subscriptionRepository.findByFollowerId(id).filter(filter).toList();
    }

    public long getFollowingCount(long id) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(id);
    }
}
