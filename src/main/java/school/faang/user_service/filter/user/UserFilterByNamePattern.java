package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserFilterByNamePattern implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto presetDto) {
        System.out.println("\n✅ :" + presetDto);
        return presetDto.getUsernamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto presetDto) {
        System.out.println("\n✅ :");
        return users
                .filter(user -> user.getUsername().toLowerCase()
                        .contains(presetDto.getUsernamePattern().toLowerCase()));
    }
}
