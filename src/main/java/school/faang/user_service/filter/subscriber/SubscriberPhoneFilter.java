package school.faang.user_service.filter.subscriber;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class SubscriberPhoneFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPhonePattern() != null && !filters.getPhonePattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getPhone().equals(filters.getPhonePattern()));
    }
}
