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
        {   //validation
            if (followerId == targetId)
                throw new DataValidationException("A user cannot follow themselves. UserId: " + targetId);
            if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, targetId))
                throw new DataValidationException("The subscription has already been issued");
        }
        subscriptionRepository.followUser(followerId, targetId);
    }
}
