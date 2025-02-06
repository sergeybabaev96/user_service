package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class SubscriberCountryFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(SubscriberFilterDto filters) {
        return filters.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriberFilterDto filters) {
        return users.filter(user -> user.getCountry() != null
                && user.getCountry().getTitle() != null
                && user.getCountry().getTitle().contains(filters.getCountryPattern()));
    }
}