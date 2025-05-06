package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalDto;
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

    public static int MAXIMUM_ALLOWED_ACTIVE_GOALS = 3;//todo вынести в конфигурацию

    private final GoalMapper goalMapper;

    private final GoalRepository goalRepository;

    private final SkillService skillService;
    private final UserService userService;

    public Goal createGoal(Long userId, Goal goal) {
        long usersActiveGoals = goalRepository.findGoalsByUserId(userId)
                .filter(GoalService::goalIsActive)
                .count();

        if (usersActiveGoals >= MAXIMUM_ALLOWED_ACTIVE_GOALS) {
            throw new IllegalArgumentException("User exceeded maximum allowed number or active goals "
                    + usersActiveGoals);
        }

        List<Skill> skillsOfUser = skillService.findAllByUserId(userId);
        List<Skill> missingSkills = goal.getSkillsToAchieve().stream()
                .filter(skillsOfUser::contains)
                .toList();

        if (!missingSkills.isEmpty()) {
            throw new IllegalArgumentException("User hasn't required skills for the goal: " + missingSkills);
        }
        //todo проверить добавление цели у юзера и все другие связанные проверки
        return goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent().getId()
        );
    }

    public Goal updateGoal(Long goalId, GoalDto goalDto) {
        var goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("No goal found by id " + goalId));
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
            Set<Skill> merged = new HashSet<>(skills);
            merged.addAll(user.getSkills());
            user.setSkills(new ArrayList<>(merged));
        });
        skills.forEach(skill -> {
            Set<User> merged = new HashSet<>(users);
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
        deleteGoalRecursive(goalToDelete);

        return goalToDelete;
    }

    private void deleteGoalRecursive(Goal goalToDelete) {
        var users = goalToDelete.getUsers();
        users.forEach(user -> user.getGoals().remove(goalToDelete));
        userService.updateAll(users);

        var skills = goalToDelete.getSkillsToAchieve();
        skills.forEach(skill -> skill.getGoals().remove(goalToDelete));
        skillService.updateAll(skills);

        var invitations = goalToDelete.getInvitations();
        //todo on next task with invitations
    }

    public Goal findById(Long id) {
        return goalRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Goal with id " + id));
    }

    public static boolean goalIsActive(@NotNull Goal goal) {
        return GoalStatus.ACTIVE == goal.getStatus();
    }

    public static boolean goalIsCompleted(@NotNull Goal goal) {
        return GoalStatus.COMPLETED == goal.getStatus();
    }
}
