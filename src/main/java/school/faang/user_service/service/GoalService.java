package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.goal.GoalNotExistException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserContext userContext;
    private final GoalValidator goalValidator;

    @Transactional
    public Goal createGoal(Goal newGoalData, List<Long> skillsId, Long parentId) {
        long userId = userContext.getUserId();
        goalValidator.validateMaxActiveGoalLimit(goalRepository.countActiveGoalsPerUser(userId));

        if (parentId != null) {
            goalRepository.findById(parentId).orElseThrow(() -> new GoalNotExistException(parentId));
        }
        Goal newGoal = goalRepository.create(newGoalData.getTitle(), newGoalData.getDescription(), parentId);
        goalRepository.assignGoalToUser(userId, newGoal.getId());

        if (!skillsId.isEmpty()) {
            skillService.assignSkillToGoal(newGoal.getId(), skillsId);
        }
        return newGoal;
    }
}