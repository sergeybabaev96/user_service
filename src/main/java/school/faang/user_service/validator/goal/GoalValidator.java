package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;

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
}