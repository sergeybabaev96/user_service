package school.faang.user_service.validator.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.skill.SkillService;
import java.util.List;

@Component
@Slf4j
public class GoalServiceValidator {
    private static final int MAX_ACTIVE_GOALS_PER_USER = 5; // Пример максимального количества активных целей

    public static void validateActiveGoalsCount(Long userId, int activeGoalsCount) {
        if (activeGoalsCount >= MAX_ACTIVE_GOALS_PER_USER) {
            log.error("Пользователь с идентификатором {} превысил максимальное количество активных целей", userId);
            throw new IllegalArgumentException("Целей не может быть больше " + MAX_ACTIVE_GOALS_PER_USER);
        }
    }

    public static void validateSkillIds(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            log.error("Идентификаторы навыков отсутствуют или пусты");
            throw new IllegalArgumentException("Список навыков не может быть пустым");
        }
    }

    public static void validateSkillsExistence(SkillService skillService, List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!skillService.existsById(skillId)) {
                log.error("Навык с ID {} отсутствует", skillId);
                throw new IllegalArgumentException("Навык с ID " + skillId + " не найден");
            }
        }
    }

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            log.error("Заголовок цели отсутствует или пустой");
            throw new IllegalArgumentException("Заголовок цели не может быть пустым");
        }
    }

    public static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Описание цели отсутствует или пустое");
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }
    }
}