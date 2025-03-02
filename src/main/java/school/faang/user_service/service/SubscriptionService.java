package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;

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
}
