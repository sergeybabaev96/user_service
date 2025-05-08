package school.faang.user_service.filters.subscriptions;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class SubscriptionPhoneFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getPhonePattern()) && !filterDto.getPhonePattern().isEmpty();
    }

    @Override
    public boolean apply(User user, UserFilterDto filterDto) {
        return user.getPhone().contains(filterDto.getPhonePattern());
    }
}
