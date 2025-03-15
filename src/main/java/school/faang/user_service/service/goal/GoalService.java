package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        if (!userService.isWithinGoalLimit(userId)) {
            throw new DataValidationException("User has the maximum number of goals");
        }

        if (!goalRepository.existsById(goalDto.getParentId())) {
            throw new DataValidationException("Goal parent with id " + goalDto.getParentId() + " does not exist");
        }

        validateExistsGoalSkills(goalDto.getSkillIds());

        Long goalId = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(),
                goalDto.getParentId()).getId();

        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));

        Goal createdGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal with id " + goalId + " does not exist"));

        return goalMapper.toDto(createdGoal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal with id " + goalId + " does not exist"));

        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new DataValidationException("Cannot update a goal that is already completed");
        }
        if (GoalStatus.COMPLETED.equals(goalDto.getStatus())) {
            validateExistsGoalSkills(goalDto.getSkillIds());
            goal.getUsers().forEach(user ->
                    goal.getSkillsToAchieve().forEach(skill ->
                            skillService.assignSkillToUser(user.getId(), skill.getId())));
        }
        updateGoalSkills(goal, goalDto.getSkillIds());
        Goal updatedGoal = goalRepository.save(goal);

        return goalMapper.toDto(updatedGoal);
    }

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new DataValidationException("Goal with id " + goalId + " does not exist");
        }

        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        return applyFilters(goals, filters)
                .map(goalMapper::toDto)
                .toList();
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        return applyFilters(goals, filters)
                .map(goalMapper::toDto)
                .toList();
    }

    private Stream<Goal> applyFilters(Stream<Goal> goals, GoalFilterDto filters) {
        for (GoalFilter filter : goalFilters) {
            if (filter.isApplicable(filters)) {
                goals = filter.apply(filters, goals);
            }
        }
        return goals;
    }

    private void validateExistsGoalSkills(List<Long> skillIds) {
        if (!skillService.isAllSkillsExist(skillIds)) {
            throw new RuntimeException("Skill ids do not exists");
        }
    }

    private void updateGoalSkills(Goal goal, List<Long> newSillIds) {
        goalRepository.removeSkillsFromGoal(goal.getId());
        newSillIds.forEach(sillId -> goalRepository.addSkillToGoal(sillId, goal.getId()));
    }
}