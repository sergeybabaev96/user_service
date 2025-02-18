package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getExperienceMax() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (filters == null || filters.getExperienceMax() == 0 || users == null) {
            return Stream.empty();
        }
        return users.filter(user -> user.getExperience() != null
                && user.getExperience() <= filters.getExperienceMax());
    }
}
