package school.faang.user_service.serviceImpl.subscription_filters;

import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

public interface UserFilterStrategy {
    boolean filterUsers(User user, UserDtoFilter userDtoFilter);
}
