package school.faang.user_service.service.goal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class GoalService {
    private GoalRepository goalRepository;

    public void updateGoal(Long goalId, Goal goal) {
    }

    public void deleteGoal(Goal goal) {
        List<User> usersOfGoal = goal.getUsers();
        goal.getUsers().removeAll(usersOfGoal);
        goalRepository.save(goal);
        goalRepository.deleteByGoalId(goal.getId());
    }
}
