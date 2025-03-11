package school.faang.user_service.service.subscription.filter;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class SubscriptionUserContactFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return !StringUtils.isBlank(filter.contactPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getContacts().stream()
                .anyMatch(c -> c.getContact().matches(filter.contactPattern())));
    }
}
