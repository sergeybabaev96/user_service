package school.faang.user_service.filters.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
@Component
public class UserNamePattern implements UserFilter{


    @Override
    public boolean isApplicable(UserFilterDto filter) {
       return filter.username() != null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filter.username().equals(user.getUsername()))
                .toList();
    }
}
