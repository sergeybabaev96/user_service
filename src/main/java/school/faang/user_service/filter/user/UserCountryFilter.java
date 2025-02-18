package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (!validateParameters(users, filters) || filters.getCountryPattern() == null) {
            return Stream.empty();
        }

        return users.filter(user -> user.getCountry() != null && user.getCountry().getTitle() != null
                && user.getCountry().getTitle()
                .toUpperCase()
                .contains(filters.getCountryPattern().toUpperCase()));
    }
}
