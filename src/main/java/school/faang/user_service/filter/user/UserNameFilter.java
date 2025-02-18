package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (filters == null || users == null || filters.getNamePattern() == null) {
            return Stream.empty();
        }
        return users.filter(user -> user.getUsername() != null && user.getUsername()
                .toUpperCase().contains(filters.getNamePattern().toUpperCase()));
    }
}
