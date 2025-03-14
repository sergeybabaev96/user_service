package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
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

    public GoalDto createGoal(long userId, GoalDto goalDto) {
        validateUserGoalCount(userId);
        validateExistsGoalParent(goalDto);
        validateExistsGoalSkills(goalDto.getSkillIds());

        Long goalId = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(),
                goalDto.getParentId()).getId();

        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));
        Goal createdGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id " + goalId + " does not exist"));

        return goalMapper.toDto(createdGoal);
    }

    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id " + goalId + " does not exist"));
        validateStatuses(goalDto, goal);
        validateExistsGoalSkills(goalDto.getSkillIds());

        assignSkillsToUsers(goal);
        goalRepository.removeSkillsFromGoal(goal.getId());
        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
        return goalMapper.toDto(goalRepository.save(goal));
    }

    public void deleteGoal(long goalId) {
        validateExistsGoal(goalId);
        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        return applyFiltersAndConvertToDtos(goals, filters);
    }

    public List<GoalDto> getGoalsByUserId(long userId, GoalFilterDto filters) {
        Stream<Goal> userGoals = goalRepository.findGoalsByUserId(userId);
        return applyFiltersAndConvertToDtos(userGoals, filters);
    }

    private void validateUserGoalCount(long userId) {
        if (!userService.isWithinGoalLimit(userId)) {
            throw new DataValidationException("User has the maximum number of goals");
        }
    }

    private void validateExistsGoalParent(GoalDto goalDto) {
        if (!goalRepository.existsById(goalDto.getParentId())) {
            throw new EntityNotFoundException("Goal parent with id " + goalDto.getParentId() + " does not exist");
        }
    }

    private void assignSkillsToUsers(Goal goal) {
        goal.getUsers().forEach(user ->
                goal.getSkillsToAchieve().forEach(skill ->
                        skillService.assignSkillToUser(user.getId(), skill.getId())));
    }

    private static void validateStatuses(GoalDto goalDto, Goal goal) {
        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new DataValidationException("Cannot update a goal that is already completed");
        }
        if (!GoalStatus.COMPLETED.equals(goalDto.getStatus())) {
            throw new DataValidationException("goalDto status should be completed");
        }
    }

    private void validateExistsGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal with id " + goalId + " does not exist");
        }
    }

    private void validateExistsGoalSkills(List<Long> skillIds) {
        if (!skillService.isAllSkillsExist(skillIds)) {
            throw new EntityNotFoundException("Some skill id do not exists");
        }
    }

    private List<GoalDto> applyFiltersAndConvertToDtos(Stream<Goal> goals, GoalFilterDto filters) {
        return goalFilters.stream()
                .reduce(goals, (stream, filter) -> filter.apply(stream, filters), (s1, s2) -> s2)
                .map(goalMapper::toDto)
                .toList();
    }
}