package school.faang.user_service.validation.subscriptions;

import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;

@Component
public class SubscriptionValidations {

    public void ValidateFollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant subscribe to yourself");
        }
    }

    public void ValidateUnfollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant unsubscribe to yourself");
        }
    }

    public void ValidateSubscribeAction(boolean existSub) {
        if (existSub) {
            throw new DataValidationException("You are already subscribed to this user");
        }
    }

    public void ValidateUnsubscribeAction(boolean existSub) {
        if (!existSub) {
            throw new DataValidationException("You cant unsubscribe to user that you are not subscribed");
        }
    }

    private boolean checkFollowForSelf(long followerId, long followeeId) {
        return followerId == followeeId;
    }
}
