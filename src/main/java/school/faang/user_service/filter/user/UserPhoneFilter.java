package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getPhonePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (users == null || filters == null || filters.getPhonePattern() == null) {
            return Stream.empty();
        }

        return users.filter(user -> user.getPhone() != null && user.getPhone()
                .toUpperCase().contains(filters.getPhonePattern().toUpperCase()));
    }
}
