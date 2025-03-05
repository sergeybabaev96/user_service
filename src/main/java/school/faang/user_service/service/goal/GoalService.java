package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.goal.GoalDtoValidator;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final GoalValidator goalValidator;
    private final SkillValidator skillValidator;
    private final GoalDtoValidator goalDtoValidator;


    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalDtoValidator.validateGoalDto(goalDto, userId);


        // Проверка количество целей
        goalValidator.validateCountGoals(goalRepository.countActiveGoalsPerUser(userId), userId);

        // Проверка на наличие
        List<Long> skillIds = goalDto.getSkillIds();
        skillValidator.validSkills(skillIds, userId);

        // Проверка, что все навыки существуют в базе данных
        skillIds.forEach(this::isExistsSkillId);
        
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        log.info("Цель успешно создана для пользователя с идентификатором: {}", userId);


        // Возвращаем DTO созданной цели
        return goalMapper.toDto(goal);
    }

    private void isExistsSkillId(Long skillId) {
        if (!skillService.existsById(skillId)) {
            log.error("Навык с ID {} отсутствует", skillId);
            throw new IllegalArgumentException("Навык с ID не найден" + skillId);
        }
    }
}
