package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilter;
    private final UserService userService;
    private final UserRepository userRepository;

    public GoalViewDto createGoal(@NotNull Long userId, @NotNull GoalCreateDto goal) {
        goalValidator.validateCreation(userId, goal);
        Goal goalEntity = goalMapper.toEntity(goal);
        userAddGoal(userId, goalEntity);
        return goalMapper.toDto(goalRepository.save(goalEntity));
    }

    public GoalViewDto updateGoal(@NotNull Long goalId, @NotNull GoalCreateDto goalDto) {
        goalValidator.validateUpdate(goalId, goalDto);
        Goal goal = findGoalById(goalId);
        assignSkillsToUser(goal, goalDto);
        goalMapper.update(goalDto, goal);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    public void deleteGoal(@NotNull Long goalId) {
        Goal goal = findGoalById(goalId);
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        users.forEach(user -> {
            user.getGoals().remove(goal);
            userRepository.save(user);
        });
        goalRepository.delete(goal);
    }

    public List<GoalViewDto> findSubtasksByGoalId(@NotNull Long goalId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findByParent(goalId);
        return applyFilters(goals, filter);
    }

    public List<GoalViewDto> getGoalsByUser(@NotNull Long userId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findGoalsByUserId(userId);
        return applyFilters(goals, filter);
    }

    private void userAddGoal(Long userId, Goal goal) {
        User user = userService.getUser(userId);
        if (!user.getGoals().contains(goal)) {
            user.getGoals().add(goal);
            userRepository.save(user);
        } else {
            throw new EntityAlreadyExistException("У пользователя " + userId + " уже есть цель " + goal);
        }
    }

    private List<GoalViewDto> applyFilters(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilter) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.apply(goals, filter);
            }
        }
        return goals
                .map(goalMapper::toDto)
                .toList();
    }

    private Goal findGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель " + goalId + " не найдена"));
    }

    private void assignSkillsToUser(Goal goal, GoalCreateDto goalDto) {
        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            List<Skill> skills = goal.getSkillsToAchieve();
            List<User> users = goalRepository.findUsersByGoalId(goal.getId());
            users.forEach(user -> {
                skills.stream()
                        .filter(skill -> !user.getSkills().contains(skill))
                        .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
                userRepository.save(user);
            });
        }
    }
}
