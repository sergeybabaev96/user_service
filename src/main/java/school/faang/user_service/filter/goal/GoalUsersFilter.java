package school.faang.user_service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalUsersFilter implements GoalFilter {
    private final UserRepository userRepository;


    @Override
    public boolean isApplicable(SearchGoalDto searchGoalDto) {
        return Objects.nonNull(searchGoalDto.userIds()) && !searchGoalDto.userIds().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoalDto) {
        List<User> allUserByIds = userRepository.findAllById(searchGoalDto.userIds());
        return goals.filter(goal -> !goal.getUsers().isEmpty()
                && new HashSet<>(goal.getUsers()).containsAll(allUserByIds));
    }
}
