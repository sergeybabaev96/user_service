package school.faang.user_service.validator.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoalValidator {
    private static final int MAX_ACTIVE_GOALS_PER_USER = 3;

    public void validateCountGoals(int activeGoals, long userId) {
        if (activeGoals >= MAX_ACTIVE_GOALS_PER_USER) {
            log.error("Пользователь с идентификатором {} превысил максимальное количество активных целей ", userId);
            throw new IllegalArgumentException("Целей не может быть больше " + MAX_ACTIVE_GOALS_PER_USER);
        }
    }
}
