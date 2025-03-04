package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.goal.GoalServiceValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);

        // Вызов методов валидации из GoalServiceValidator
        GoalServiceValidator.validateActiveGoalsCount(userId, activeGoalsCount);
        GoalServiceValidator.validateSkillIds(goalDto.getSkillIds());
        GoalServiceValidator.validateSkillsExistence(skillService, goalDto.getSkillIds());
        GoalServiceValidator.validateTitle(goalDto.getTitle());
        GoalServiceValidator.validateDescription(goalDto.getDescription());

        log.info("Создание новой цели для пользователя с идентификатором: {}", userId);
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        for (Long skillId : goalDto.getSkillIds()) {
            goalRepository.addSkillToGoal(skillId, goal.getId());
        }
        log.info("Цель успешно создана для пользователя с идентификатором: {}", userId);

        // Возвращаем DTO созданной цели
        return goalMapper.toDto(goal);
    }
}
