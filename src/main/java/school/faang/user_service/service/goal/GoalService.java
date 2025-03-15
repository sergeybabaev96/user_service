package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalCreateDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.GoalViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilter;

    public GoalViewDto createGoal(@NotNull Long userId, @NotNull GoalCreateDto goal) {
        goalValidator.validateCreation(userId, goal);
        Goal goalEntity = goalMapper.toEntity(goal);
        userAddGoal(userId, goalEntity);
        Long parentId = (goalEntity.getParent() != null) ? goalEntity.getParent().getId() : null;
        Goal savedGoal = goalRepository.create(
                goalEntity.getTitle(),
                goalEntity.getDescription(),
                parentId);
        savedGoal.setSkillsToAchieve(goalEntity.getSkillsToAchieve());
        return goalMapper.toDto(goalRepository.save(goalEntity));
    }

    public GoalViewDto updateGoal(@NotNull Long goalId, @NotNull GoalCreateDto goal) {
        goalValidator.validateUpdate(goalId, goal);
        Goal goalEntity = goalMapper.toEntity(goal);
        List<Skill> skills = goalEntity.getSkillsToAchieve();
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель не найдена"));
        if (goalEntity.getStatus() == GoalStatus.COMPLETED) {
            goalRepository.findUsersByGoalId(goalId).forEach(user -> {
                skills.stream()
                        .filter(skill -> !user.getSkills().contains(skill))
                        .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
                goalToUpdate.setStatus(GoalStatus.COMPLETED);
            });
        }
        goalToUpdate.getSkillsToAchieve().clear();
        goalToUpdate.getSkillsToAchieve().addAll(skills);
        return goalMapper.toDto(goalRepository.save(goalToUpdate));
    }

    public void deleteGoal(@NotNull Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель " + goalId + " не найдена"));
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        users.forEach(user -> user.getGoals().remove(goal));
        goalRepository.delete(goal);
    }

    public List<GoalViewDto> findSubtasksByGoalId(@NotNull Long goalId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findByParent(goalId);
        applyFilters(goals, filter);
        return goals
                .map(goalMapper::toDto)
                .toList();
    }

    public List<GoalViewDto> getGoalsByUser(@NotNull Long userId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findGoalsByUserId(userId);
        applyFilters(goals, filter);
        return goals
                .map(goalMapper::toDto)
                .toList();
    }

    private void userAddGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        if (!user.getGoals().contains(goal)) {
            user.getGoals().add(goal);
        } else {
            throw new EntityAlreadyExistException("У пользователя " + userId + " уже есть цель " + goal);
        }
    }

    private void applyFilters(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilter) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.apply(goals, filter);
            }
        }
    }
}
