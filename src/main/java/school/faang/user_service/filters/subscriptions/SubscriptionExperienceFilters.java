package school.faang.user_service.filters.subscriptions;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.time.temporal.ValueRange;
import java.util.Objects;

@Component
public class SubscriptionExperienceFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getExperienceMin() > 0 || filterDto.getExperienceMax() > 0;
    }

    @Override
    public boolean apply(User user, UserFilterDto filterDto) {
        return Objects.nonNull(user.getExperience()) && ValueRange.of(filterDto.getExperienceMin(),
                filterDto.getExperienceMax()).isValidIntValue(user.getExperience());
    }
}
