package school.faang.user_service.validation.subscriptions;

import school.faang.user_service.exceptions.DataValidationException;

public class SubscriptionValidation {

    public static void validateFollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant subscribe to yourself");
        }
    }

    public static void validateUnfollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant unsubscribe to yourself");
        }
    }

    public static void validateSubscribeAction(boolean existSub) {
        if (existSub) {
            throw new DataValidationException("You are already subscribed to this user");
        }
    }

    public static void validateUnsubscribeAction(boolean existSub) {
        if (!existSub) {
            throw new DataValidationException("You cant unsubscribe to user that you are not subscribed");
        }
    }

    private static boolean checkFollowForSelf(long followerId, long followeeId) {
        return followerId == followeeId;
    }
}
