package school.faang.user_service.service.subscription.filter;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface SubscriptionFilter {
    boolean isApplicable(SubscriptionUserFilterDto filter);

    Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter);
}
