package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserCountryFilter implements Filter<User, UserFilterDto> {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.countryId() != null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(u -> filters.countryId().equals(u.getCountry().getId()))
                .toList();
    }
}
