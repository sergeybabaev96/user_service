package school.faang.user_service.dto.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserDto user) {
        return user != null && user.getUsername() != null && !user.getUsername().isEmpty();
    }

    @Override
    public List<User> apply(List<User> users, UserDto user) {
        return users.stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .toList();
    }
}
