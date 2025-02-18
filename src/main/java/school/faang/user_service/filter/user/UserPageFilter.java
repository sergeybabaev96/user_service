package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getPage() > 0 && filters.getPageSize() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (users == null || filters == null) {
            return Stream.empty();
        }
        return users.limit((long) filters.getPageSize() * filters.getPage());
    }
}
