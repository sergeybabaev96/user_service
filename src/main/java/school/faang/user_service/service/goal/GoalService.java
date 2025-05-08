package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.util.goal.GoalUtil;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoalService {

    public static int MAXIMUM_ALLOWED_ACTIVE_GOALS = 3;//todo вынести в конфигурацию компонента

    private final GoalMapper goalMapper;

    private final GoalRepository goalRepository;

    private final SkillService skillService;
    private final UserService userService;

    public static boolean goalIsActive(@NotNull Goal goal) {
        return GoalStatus.ACTIVE == goal.getStatus();
    }

    public static boolean goalIsCompleted(@NotNull Goal goal) {
        return GoalStatus.COMPLETED == goal.getStatus();
    }

    public Goal createGoal(Long userId, Goal goal) {
        long usersActiveGoals = goalRepository.findGoalsByUserId(userId)
                .filter(GoalService::goalIsActive)
                .count();

        if (usersActiveGoals >= MAXIMUM_ALLOWED_ACTIVE_GOALS) {
            throw new IllegalArgumentException("User exceeded maximum allowed number or active goals "
                    + usersActiveGoals);
        }

        var skillsOfUser = skillService.findAllByUserId(userId);
        var missingSkills = goal.getSkillsToAchieve().stream()
                .filter(skillsOfUser::contains)
                .toList();

        if (!missingSkills.isEmpty()) {
            throw new IllegalArgumentException("User hasn't required skills for the goal: " + missingSkills);
        }
        Goal createdGoal = goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent().getId()
        );

        addGoalToUser(userId, createdGoal);
        addGoalToSkills(createdGoal);

        return createdGoal;
    }

    public Goal updateGoal(Long goalId, GoalDto goalDto) {
        var goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(NoSuchElementException::new);

        boolean goalWasCompleted = goalToUpdate.getStatus() == GoalStatus.COMPLETED;
        boolean goalSetCompleted = goalDto.getStatus() == GoalStatus.COMPLETED;
        if (goalSetCompleted && goalWasCompleted)
            throw new IllegalStateException("Goal was already completed");

        List<Long> copyOfUpdatesSkillIds = new ArrayList<>(goalDto.getSkillIds());
        copyOfUpdatesSkillIds.removeAll(
                skillService.findAllById(copyOfUpdatesSkillIds).stream()
                        .map(Skill::getId)
                        .toList()
        );
        if (!copyOfUpdatesSkillIds.isEmpty())
            throw new IllegalArgumentException("Skill ids not exists: ".formatted(copyOfUpdatesSkillIds.toArray()));

        goalMapper.updateGoalFromDto(goalDto, goalToUpdate);
        GoalUtil.updateTime(goalToUpdate, LocalDateTime.now());
        goalRepository.save(goalToUpdate);

        if (GoalStatus.COMPLETED == goalDto.getStatus()) {
            updateUsersWithSkills(goalToUpdate);
        }

        return goalToUpdate;
    }

    @Transactional
    void updateUsersWithSkills(Goal completedGoal) {
        var users = completedGoal.getUsers();
        var skills = completedGoal.getSkillsToAchieve();
        users.forEach(user -> {
            var merged = new HashSet<>(skills);
            merged.addAll(user.getSkills());
            user.setSkills(new ArrayList<>(merged));
        });
        skills.forEach(skill -> {
            var merged = new HashSet<>(users);
            merged.addAll(skill.getUsers());
            skill.setUsers(new ArrayList<>(merged));
        });
        skillService.updateAll(skills);
        userService.updateAll(users);
    }

    public Goal deleteGoal(long goalId) {
        var goalToDelete = goalRepository.findById(goalId)
                .orElseThrow(NoSuchElementException::new);
        goalRepository.delete(goalToDelete);
        deleteGoalCascade(goalToDelete);

        return goalToDelete;
    }

    private void deleteGoalCascade(Goal goalToDelete) {
        var users = goalToDelete.getUsers();
        users.forEach(user -> user.getGoals().remove(goalToDelete));
        userService.updateAll(users);

        var skills = goalToDelete.getSkillsToAchieve();
        skills.forEach(skill -> skill.getGoals().remove(goalToDelete));
        skillService.updateAll(skills);

        //var invitations = goalToDelete.getInvitations();
        //todo on next task with invitations
    }

    public List<Goal> findSubtasksByGoalId(long goalId) {
        GoalFilterDto blankFilter = new GoalFilterDto();
        return findSubtasksByGoalId(goalId, blankFilter);
    }

    public List<Goal> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        return goalRepository.findByParent(goalId)
                .filter(goal -> GoalUtil.goalFilter(goal, filter))
                .toList();
    }

    public List<Goal> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        return goalRepository.findGoalsByUserId(userId)
                .filter(goal -> GoalUtil.goalFilter(goal, filter))
                .toList();
    }

    public Goal findById(Long id) {
        return goalRepository
                .findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    private User addGoalToUser(Long userId, Goal createdGoal) {
        var userById = userService.findById(userId);
        return addGoalToUser(userById, createdGoal);
    }

    private User addGoalToUser(User user, Goal createdGoal) {
        var goals = user.getGoals();
        goals.add(createdGoal);
        user.setGoals(goals);
        return userService.updateUser(user);
    }

    private void addGoalToSkills(Goal createdGoal) {
        var skillsToUpdateWithNewGoal = createdGoal.getSkillsToAchieve();
        skillsToUpdateWithNewGoal.forEach(skill -> {
            List<Goal> skillGoals = skill.getGoals();
            skillGoals.add(createdGoal);
            skill.setGoals(skillGoals);
        });
        skillService.updateAll(skillsToUpdateWithNewGoal);
    }

}
