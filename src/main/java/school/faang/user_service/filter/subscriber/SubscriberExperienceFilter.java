package school.faang.user_service.filter.subscriber;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class SubscriberExperienceFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(SubscriberFilterDto filters) {
        return filters.getExperienceMin() > 0 || filters.getExperienceMax() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriberFilterDto filters) {
        int minExperience = filters.getExperienceMin();
        int maxExperience = filters.getExperienceMax();

        return users.filter(user -> user.getExperience() != null
                && user.getExperience() >= minExperience
                && user.getExperience() <= maxExperience);
    }
}