package school.faang.user_service.service.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
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
    private final GoalValidator goalValidator;
    private final SkillValidator skillValidator;
    private final GoalDtoValidator goalDtoValidator;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalDtoValidator.validateGoalDto(goalDto, userId);

        goalValidator.validateCountGoals(goalRepository.countActiveGoalsPerUser(userId), userId);

        List<Long> skillIds = goalDto.getSkillIds();
        skillValidator.validSkills(skillIds, userId);

        skillIds.forEach(this::isExistsSkillId);

        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        log.info("Цель успешно создана для пользователя с идентификатором: {}", userId);

        for (Long skillId : goalDto.getSkillIds()) {
            goalRepository.addSkillToGoal(skillId, goal.getId());
        }

        return goalMapper.toDto(goal);
    }

    private void isExistsSkillId(Long skillId) {
        if (!skillService.existsById(skillId)) {
            log.error("Навык с ID {} отсутствует", skillId);
            throw new IllegalArgumentException("Навык с ID не найден" + skillId);
        }
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Цель не найдена у id: " + goalId));

        goalValidator.validate(goalDto);
        goalValidator.validateUpdateStatus(existingGoal, goalDto);
        skillValidator.validate(goalDto.getSkillIds());

        Goal updatedGoal = goalMapper.dtoToEntity(goalDto);
        updatedGoal.setId(existingGoal.getId());

        if (GoalStatus.COMPLETED.equals(goalDto.getStatus())) {
            assignSkillsToParticipants(existingGoal, goalDto.getSkillIds());
        }

        updateSkillsForGoal(existingGoal, goalDto.getSkillIds());

        goalRepository.save(updatedGoal);
        return goalDto;
    }

    private void assignSkillsToParticipants(Goal goal, List<Long> skillIds) {
        List<User> participants = goalRepository.findUsersByGoalId(goal.getId());

        participants.forEach(participant -> skillIds.forEach(skillId ->
                goalRepository.addSkillToUser(participant.getId(), skillId)));
    }

    private void updateSkillsForGoal(Goal goal, List<Long> newSkillIds) {
        goalRepository.removeSkillsFromGoal(goal.getId());
        newSkillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
    }
}