package school.faang.user_service.dto.user;

import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {
    boolean isApplicable(UserDto user);

    List<User> apply(List<User> users, UserDto user);
}
