package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserFilterByAboutMePattern implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto presetDto) {
        return presetDto.getAboutPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto presetDto) {
        return users
                .filter(user -> user.getAboutMe().toLowerCase()
                        .contains(presetDto.getAboutPattern().toLowerCase()));
    }
}
