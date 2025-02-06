package school.faang.user_service.validation.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SubscriptionValidator {
    public void validateNotSelfSubscription(String message, long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}
