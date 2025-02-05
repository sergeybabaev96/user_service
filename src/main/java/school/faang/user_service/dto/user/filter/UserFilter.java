package school.faang.user_service.dto.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {

    boolean isApplicable(UserFilterDto dto);

    List<User> apply(List<User> users, UserFilterDto filters);
}
