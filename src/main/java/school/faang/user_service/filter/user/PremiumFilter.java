package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class PremiumFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getPremium() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> elements, UserFilterDto filter) {
        return elements.filter(user -> user.getPremium() != null);    }
}
