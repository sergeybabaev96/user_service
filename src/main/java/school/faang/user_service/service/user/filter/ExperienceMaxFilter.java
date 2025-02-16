package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ExperienceMaxFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getExperienceMax() > 0;
    }

    @Override
    public List<User> apply(Stream<User> users, UserFilterDto filter) {
        return users
                .filter(user -> filter.getExperienceMax() >= user.getExperience())
                .toList();
    }
}