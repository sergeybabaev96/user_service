package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserFilterByExperienceMoreThan implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto presetDto) {
        return null != presetDto.getExperienceMoreThan() && presetDto.getExperienceMoreThan() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto presetDto) {
        return users
                .filter(user -> user.getExperience() >= presetDto.getExperienceMoreThan());
    }
}
