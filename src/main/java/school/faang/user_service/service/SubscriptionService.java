package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserFilter;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

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

    public List<User> getFollowers(long targetId) {
        return subscriptionRepository.findByFolloweeId(targetId).toList();
    }

    public List<User> getFollowers(long targetId, UserFilter filter) {
        return subscriptionRepository.findByFolloweeId(targetId).filter(filter).toList();
    }
}
