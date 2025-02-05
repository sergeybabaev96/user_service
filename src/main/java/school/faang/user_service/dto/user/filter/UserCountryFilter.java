package school.faang.user_service.dto.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.getCountryId() != null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(u -> filters.getCountryId().equals(u.getCountry().getId()))
                .toList();
    }
}
