package school.faang.user_service.service.filter;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
@Data
public class UserFilterCombination {
    private final List<UserFilterStrategy> userFilterStrategies;

    public boolean filterUser(User user, UserDtoFilter userDtoFilter) {
        return userFilterStrategies.stream()
                .filter(strategy -> strategy.isApplicable(userDtoFilter))
                .allMatch(strat -> strat.filterUsers(user, userDtoFilter));
    }
}
