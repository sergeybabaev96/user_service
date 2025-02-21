package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
public class GoalService {
    @Autowired
    private GoalRepository goalRepository;

    public void updateGoal(Long goalId, Goal goal) {
    }

    public void deleteGoal(Long goalId) {
    }
}
