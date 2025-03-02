package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    public void followUser(long followerId, long targetId) {
        subscriptionService.followUser(followerId, targetId);
    }
}
