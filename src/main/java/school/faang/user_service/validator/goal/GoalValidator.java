package school.faang.user_service.validator.goal;

import com.github.dockerjava.api.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

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
    public void validate(GoalDto goalDto) {
        if (goalDto == null) {
            throw new BadRequestException("Цели не могут быть нулевыми");
        }

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new BadRequestException("Заголовок не может быть пустым");
        }

        if (goalDto.getSkillIds() != null && goalDto.getSkillIds().isEmpty()) {
            throw new BadRequestException("Скиллы и Идентификаторы не могут быть пустыми");
        }
    }

    public void validateUpdateStatus(Goal existingGoal, GoalDto goalDto) {
        if (GoalStatus.COMPLETED.equals(existingGoal.getStatus()) && !GoalStatus.ACTIVE.equals(goalDto.getStatus())) {
            throw new BadRequestException("Не удается обновить завершенную цель");
        }
    }
}

