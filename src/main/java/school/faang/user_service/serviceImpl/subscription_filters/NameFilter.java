package school.faang.user_service.serviceImpl.subscription_filters;

import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

public class NameFilter implements UserFilterStrategy {
    @Override
    public boolean filterUsers(User user, UserDtoFilter userDtoFilter) {
        return user.getAboutMe().contains(userDtoFilter.getNamePattern());
    }
}
