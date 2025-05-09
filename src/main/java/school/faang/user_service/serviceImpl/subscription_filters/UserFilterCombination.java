package school.faang.user_service.serviceImpl.subscription_filters;

import lombok.Data;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
public class UserFilterCombination {
    private final List<UserFilterStrategy> userFilterStrategies;

    public boolean filterUser(User user, UserDtoFilter userDtoFilter) {
        return userFilterStrategies.stream()
                .allMatch(strat -> strat.filterUsers(user, userDtoFilter));
    }
}
