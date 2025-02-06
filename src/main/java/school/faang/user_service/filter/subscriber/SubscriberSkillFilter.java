package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class SubscriberSkillFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(SubscriberFilterDto filters) {
        return filters.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriberFilterDto filters) {
        return users.filter(user -> user.getSkills() != null
                && user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle() != null
                        && skill.getTitle().contains(filters.getSkillPattern())));
    }
}