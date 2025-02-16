package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    List<User> apply(Stream<User> users, UserFilterDto filters);
}
