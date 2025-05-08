package school.faang.user_service.filters.subscriptions;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface SubscriptionFilter {
    boolean apply(User user, UserFilterDto filters);

    boolean isApplicable(UserFilterDto filters);
}
