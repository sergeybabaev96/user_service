package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.controller.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {

        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (existSub) {
            throw new DataValidationException("You are already subscribed to this user");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {

        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (!existSub) {
            throw new DataValidationException("You cant unsubscribe to user that you are not subscribed");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
