package school.faang.user_service.filters.subscriptions;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;

@Component
public class SubscriptionPhoneFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getPhonePattern()) && !filterDto.getPhonePattern().isEmpty();
    }

    @Override
    public boolean apply(User user, UserFilterDto filterDto) {
        if (Objects.isNull(user.getPhone())) {
            return false;
        }
        return user.getPhone().contains(filterDto.getPhonePattern());
    }
}
