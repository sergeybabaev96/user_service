package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;
import school.faang.user_service.exception.goal.UpdateComleteGoalException;
import school.faang.user_service.exception.goal.UpdateGoalWithActiveSubGoalsException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    @Value("${goal.maxLimit}")
    private Integer goalLimit;
    private final UserContext userContext;

    public void validateMaxActiveGoalLimit(long activeGoals) {
        boolean isUserReachActiveGoalLimit = activeGoals >= goalLimit;
        if (isUserReachActiveGoalLimit) {
            throw new MaxActiveGoalPerUserException(userContext.getUserId(), goalLimit);
        }
    }

    public void validateUpdateCompleteGoal(Goal goal) {
        boolean goalIsComplete = goal.getStatus().equals(GoalStatus.COMPLETED);
        if (goalIsComplete) {
            throw new UpdateComleteGoalException(goal.getId());
        }
    }

    public void validateAllSubGoalsCompleted(long goalId, Stream<Goal> subGoals) {
        String activeSubGoalIds = subGoals.filter(goal -> !goal.getStatus().equals(GoalStatus.COMPLETED))
                .map(subGoal -> String.valueOf(subGoal.getId()))
                .collect(Collectors.joining(", "));
        if (!activeSubGoalIds.isEmpty()) {
            throw new UpdateGoalWithActiveSubGoalsException(goalId, activeSubGoalIds);
        }
    }
}