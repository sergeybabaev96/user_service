package school.faang.user_service.service.subscription.filter;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class SubscriptionUserSkillFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return !StringUtils.isBlank(filter.skillPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getSkills().stream()
                .anyMatch(s -> s.getTitle().matches(filter.skillPattern())));
    }
}
