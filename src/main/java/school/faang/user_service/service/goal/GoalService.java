package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS_PER_USER = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        log.info("cоздание новой цели для пользователя с идентификатором: {}", userId);

        // Проверка на наличие количество навыков
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS_PER_USER) {
            log.error("Пользователь с идентификатором {} превысил максимальное количество активных целей ", userId);
            throw new IllegalArgumentException("Целей не может быть больше " + MAX_ACTIVE_GOALS_PER_USER);
        }

        // Проверка на наличие навыков
        List<Long> skillIds = goalDto.getSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            log.error("Идентификаторы навыков отсутствуют или пусты для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Список навыков не может быть пустым");
        }

        // Проверка, что все навыки существуют в базе данных
        for (Long skillId : skillIds) {
            if (!skillService.existsById(skillId)) {
                log.error("Навык с ID {} отсутствует", skillId);
                throw new IllegalArgumentException("Навык с ID не найден");
            }
        }

        // Проверка и Создание цели
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            log.error("Title отсутствует в goal DTO для пользователя с ID {} ", userId);
            throw new IllegalArgumentException("Заголовок цели не может быть пустым");
        }
        if (goalDto.getDescription() == null || goalDto.getDescription().isBlank()) {
            log.error("Description отсутствует в goal DTO для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }

        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        log.info("Цель успешно создана для пользователя с идентификатором: {}", userId);

        // Возвращаем DTO созданной цели
        return goalMapper.toDto(goal);
    }
}
