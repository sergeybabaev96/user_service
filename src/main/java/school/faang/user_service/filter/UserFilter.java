package school.faang.user_service.filter;

import school.faang.user_service.entity.User;

public interface UserFilter {
    boolean apply(User user, Object object);
}
