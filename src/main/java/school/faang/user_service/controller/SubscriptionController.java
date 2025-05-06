package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    void followUser(long followerId, long followeeId) {

        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("You cant subscribe on yourself");
        }

        subscriptionService.followUser(followerId, followeeId);

    }

    boolean checkFollowForSelf(long followerId, long followeeId) {
        return followerId == followeeId;
    }
}
