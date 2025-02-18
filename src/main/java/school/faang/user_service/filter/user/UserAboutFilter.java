package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getAboutPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (!validateParameters(users, filters) || filters.getAboutPattern() == null) {
            return Stream.empty();
        }
        return users.filter(user -> user.getAboutMe() != null && user.getAboutMe()
                .toUpperCase().contains(filters.getAboutPattern().toUpperCase()));
    }
}
