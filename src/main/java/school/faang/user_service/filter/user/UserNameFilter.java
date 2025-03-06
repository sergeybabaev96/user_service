package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.namePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        if (!isApplicable(filter)) {
            return users;
        }
        return users.filter(user -> user.getUsername() != null
                && user.getUsername().toLowerCase().contains(filter.namePattern().toLowerCase()));
    }
}
