package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.InactiveGoalUpdateException;
import school.faang.user_service.exception.MaxActiveGoalsExceededException;
import school.faang.user_service.exception.NoSkillsFoundException;
import school.faang.user_service.exception.NonExistentSkillException;
import school.faang.user_service.exception.UnsupportedGoalStatusException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.filters.goal.GoalFilter;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;

    @Value("${app.max-active-goals}")
    private int MAX_ACTIVE_GOALS;

    @Transactional
    public void createGoal(Long userId, Goal goal) {
        User user = getUser(userId);

        long numberOfActiveGoals = goalRepository
                .countActiveGoalsPerUser(userId);
        if (numberOfActiveGoals > MAX_ACTIVE_GOALS) {
            throw new MaxActiveGoalsExceededException("The user's number of active goals exceeds the maximum number");
        }

        existSkills(goal);

        goal.getUsers().add(user);
        user.getGoals().add(goal);

        goal.setStatus(GoalStatus.ACTIVE);
        goal.setCreatedAt(LocalDateTime.now());
        goalRepository.save(goal);
    }

    @Transactional
    public void updateGoal(Long goalId, Goal goal) {
        Goal existingGoal = getGoal(goalId);

        GoalStatus newStatus = goal.getStatus();

        switch (newStatus) {
            case ACTIVE:
                handleActiveStatus(goal, existingGoal);
                break;
            case COMPLETED:
                handleCompletedStatus(goal, existingGoal, goalId);
                break;
            default:
                throw new UnsupportedGoalStatusException("Unsupported status");
        }

        goalRepository.save(existingGoal);
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        Goal goal = getGoal(goalId);
        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public List<Goal> findSubtasksByGoalId(long parentId, GoalFilterDto filters) {
        List<Goal> subtasks = goalRepository
                .findByParent(parentId)
                .collect(Collectors.toList());

        log.info("Goal subtasks with parent id {} before filtering", parentId);
        return filterGoals(subtasks, filters);
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUserId(long userId, GoalFilterDto filters) {
        List<Goal> goals = goalRepository
                .findGoalsByUserId(userId)
                .collect(Collectors.toList());

        log.info("Goals for user with  id {} before filtering", userId);
        return filterGoals(goals, filters);
    }

    public void updateSkillsToGoal(Long goalId, List<Skill> skills) {
        List<Skill> oldSkills = goalRepository
                .findSkillsByGoalId(goalId);
        validSkills(oldSkills);

        oldSkills.forEach(skillService::deleteSkill);

        skills.forEach(skill -> skillService.assignSkillToGoal(skill.getId(), goalId));
    }

    public void assignSkillToGoal(long skillId, long goalId) {
        skillService.assignSkillToGoal(skillId, goalId);
    }

    private void mapGoalToUpdate(Goal goal, Goal existingGoal) {
        existingGoal.setParent(goal.getParent());
        existingGoal.setDescription(goal.getDescription());
        existingGoal.setStatus(goal.getStatus());
        existingGoal.setDeadline(goal.getDeadline());
        existingGoal.setMentor(goal.getMentor());
        existingGoal.setUpdatedAt(LocalDateTime.now());
    }

    private User getUser(Long userId) {
        return userService.getUser(userId);
    }

    public Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found"));
    }

    private void handleActiveStatus(Goal goal, Goal existingGoal) {
        if (Objects.equals(existingGoal.getStatus(), GoalStatus.ACTIVE)) {
            mapGoalToUpdate(goal, existingGoal);
        } else {
            throw new InactiveGoalUpdateException("Cannot update a goal that is not active");
        }
    }

    private void handleCompletedStatus(Goal goal, Goal existingGoal, Long goalId) {
        handleActiveStatus(goal, existingGoal);
        existingGoal.setStatus(GoalStatus.COMPLETED);

        List<Skill> skillsToAchieve = skillService.findSkillsByGoalId(goalId);
        List<User> users = goalRepository.findUsersByGoalId(goalId);

        users.stream()
                .flatMap(user -> skillsToAchieve.stream()
                        .map(skill -> new AbstractMap.SimpleEntry<>(skill, user)))
                .forEach(entry ->
                        skillService.assignSkillToGoal(entry.getKey().getId(), entry.getValue().getId()));
    }

    private List<Goal> filterGoals(List<Goal> goals, GoalFilterDto filters) {
        Stream<Goal> streamSubtasks = goals.stream();

        return goalFilters.stream()
                .filter(filter -> filter
                        .isApplicable(filters))
                .reduce(streamSubtasks, (currentStream, filter) -> filter
                        .apply(currentStream, filters), (s1, s2) -> s1)
                .collect(Collectors.toList());
    }

    private void existSkills(Goal goal) {
        if (goal.getSkillsToAchieve().stream()
                .noneMatch(skill -> skillService.skillExistsByTitle(skill.getTitle()))) {
            throw new NonExistentSkillException("The goal contains non-existent skills");
        }
    }

    private static void validSkills(List<Skill> skills) {
        if (skills.isEmpty()) {
            throw new NoSkillsFoundException("No skills found for the goal");
        }
    }
}