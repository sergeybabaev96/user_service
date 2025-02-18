package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserCityFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getCityPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (!validateParameters(users, filters) || filters.getCityPattern() == null) {
            return Stream.empty();
        }

        return users.filter(user -> user.getCity() != null && user.getCity()
                .toUpperCase().contains(filters.getCityPattern().toUpperCase()));
    }
}
