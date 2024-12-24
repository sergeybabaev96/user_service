package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void validateSubscriptionCreation(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(
                    String.format("User %d is already subscribed to user %d.",
                            followerId, followeeId));
        }
    }

    public void validateSubscriptionRemoval(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(
                    String.format("User %d is not subscribed to user %d.",
                            followerId, followeeId));
        }
    }

    public void validateNoSelfSubscription(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(
                    String.format("User %d cannot subscribe to their own account.", followerId));
        }
    }
}
