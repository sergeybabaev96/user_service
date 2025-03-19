package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalValidator {
    private static final int MAX_GOALS = 3;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void validateCreation(Long userId, GoalCreateDto goal) {
        validateTitle(goal);
        validateMaxActiveGoals(userId);
        validateSkillsExist(goal);
    }

    public void validateUpdate(Long goalId, GoalCreateDto goal) {
        validateStatus(goalId);
        validateTitle(goal);
        validateSkillsExist(goal);
    }

    private void validateTitle(GoalCreateDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            log.error("Ошибка валидации: название цели пустое");
            throw new DataValidationException("Название цели пустое");
        }
    }

    private void validateMaxActiveGoals(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS) {
            log.error("Ошибка валидации: максимальное количество активных целей 3");
            throw new DataValidationException("Превышено максимальное количество целей: " + MAX_GOALS);
        }
    }

    private void validateSkillsExist(GoalCreateDto goal) {
        List<Long> skillsId = goal.getSkillsToAchieveId();
        if (null != skillsId && skillRepository.countExisting(skillsId) != skillsId.size()) {
            log.error("Ошибка валидации: цель содержит несуществующие навыки");
            throw new DataValidationException("Цель содержит несуществующие навыки");
        }
    }

    private void validateStatus(Long goalId) {
        if (goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель не найдена"))
                .getStatus() == GoalStatus.COMPLETED) {
            log.error("Ошибка валидации: нельзя изменять завершенную цель");
            throw new DataValidationException("Цель уже завершена");
        }
    }
}
