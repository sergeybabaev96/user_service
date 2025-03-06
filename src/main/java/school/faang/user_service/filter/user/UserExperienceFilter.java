package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.experienceMin() > 0 || filter.experienceMax() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        int minExperience = filter.experienceMin();
        int maxExperience = filter.experienceMax();

        return users.filter(user -> user.getExperience() != 0
                && user.getExperience() >= minExperience
                && user.getExperience() <= maxExperience);
    }
}
