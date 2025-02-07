package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalRequestDto;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalRequestDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.data.GoalDataFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.operations.GoalAssignmentHelper;
import school.faang.user_service.service.goal.operations.GoalValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalDataFilter> goalDataFilters;
    private final GoalValidator goalValidator;
    private final GoalAssignmentHelper goalAssignmentHelper;

    @Transactional
    public CreateGoalResponse createGoal(CreateGoalRequestDto request) {
        goalValidator.validateActiveGoalsLimit(request.getUserId());
        goalValidator.validateSkillsExist(request.getSkillIds());

        Goal goal = goalMapper.toEntity(request);
        goal.setParent(goalValidator.findParentGoal(request.getParentId()));

        Goal createdGoal = goalRepository.save(goal);
        goalAssignmentHelper.assignSkillsToGoal(createdGoal, request.getSkillIds());

        return goalMapper.toCreateResponse(createdGoal);
    }

    @Transactional
    public UpdateGoalResponse updateGoal(UpdateGoalRequestDto request) {
        Goal existingGoal = getGoalById(request.getGoalId());
        goalValidator.validateGoalUpdatable(existingGoal);
        goalValidator.validateSkillsExist(request.getSkillIds());

        goalMapper.updateGoalFromDto(request, existingGoal);
        goalAssignmentHelper.assignSkillsToGoal(existingGoal, request.getSkillIds());

        if (request.getStatus() == GoalStatus.COMPLETED) {
            goalAssignmentHelper.assignSkillsToUsers(existingGoal, request.getSkillIds());
        }

        goalRepository.save(existingGoal);
        return goalMapper.toUpdateResponse(existingGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = getGoalById(goalId);
        goalRepository.delete(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        return goalDataFilters.stream()
                .filter(filterImpl -> filterImpl.isApplicable(filter))
                .reduce(
                        goalRepository.findByParent(goalId),
                        (stream, filterImpl) -> filterImpl.apply(stream, filter),
                        (s1, s2) -> s2
                )
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalDataFilters.stream()
                .filter(filterImpl -> filterImpl.isApplicable(filter))
                .reduce(
                        goalRepository.findGoalsByUserId(userId),
                        (stream, filterImpl) -> filterImpl.apply(stream, filter),
                        (s1, s2) -> s2
                )
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }

    private Goal getGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal not found with id: " + goalId));
    }

    @Transactional
    public List<Goal> stopGoalsByUser(Long userId) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        goals.forEach(goal -> {
            if (goal.getUsers().size() <= 1) {
                goalRepository.delete(goal);
            } else {
                goal.getUsers().removeIf(user -> user.getId().equals(userId));
            }
        });

        return goals;
    }
}