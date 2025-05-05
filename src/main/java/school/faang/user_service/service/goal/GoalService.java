package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    public static int MAXIMUM_ALLOWED_ACTIVE_GOALS = 3;//вынести в конфигурацию

    private final GoalRepository goalRepository;

    private final SkillRepository skillRepository;

    public Goal createGoal(Long userId, Goal goal) {
        long usersActiveGoals = goalRepository.findGoalsByUserId(userId)
                .filter(GoalService::goalIsActive)
                .count();

        if (usersActiveGoals > MAXIMUM_ALLOWED_ACTIVE_GOALS) {
            throw new IllegalArgumentException("User exceeded maximum allowed number or active goals "
                    + usersActiveGoals);
        }

        List<Skill> skillsOfUser = skillRepository.findAllByUserId(userId);
        List<Skill> missingSkills = goal.getSkillsToAchieve().stream()
                .filter(skillsOfUser::contains)
                .toList();

        if (!missingSkills.isEmpty()) {
            throw new IllegalArgumentException("User hasn't required skills for the goal: " + missingSkills);
        }

        return goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent().getId()
        );
    }

    public static boolean goalIsActive(Goal goal) {
        return GoalStatus.ACTIVE == goal.getStatus();
    }

    public static boolean goalIsCompleted(Goal goal) {
        return GoalStatus.COMPLETED == goal.getStatus();
    }
}
