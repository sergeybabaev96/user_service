package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class SubscriberPhoneFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(SubscriberFilterDto filters) {
        return filters.getPhonePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriberFilterDto filters) {
        return users.filter(user -> user.getPhone() != null
                && user.getPhone().contains(filters.getPhonePattern()));
    }
}