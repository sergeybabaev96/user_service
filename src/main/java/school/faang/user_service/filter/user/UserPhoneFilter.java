package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.phonePattern() != null
                && !filter.phonePattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        if (!isApplicable(filter)) {
            return users;
        }
        return users.filter(user -> user.getPhone() != null
                && !user.getPhone().isBlank()
        && user.getPhone().contains(filter.phonePattern()));
    }
}
