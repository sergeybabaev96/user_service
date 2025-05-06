package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.util.goal.GoalUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {

    public static int MAXIMUM_ALLOWED_ACTIVE_GOALS = 3;//todo вынести в конфигурацию

    private final GoalMapper goalMapper;

    private final GoalRepository goalRepository;

    private final SkillService skillService;

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

        return goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent().getId()
        );
    }

    public Goal updateGoal(Long goalId, GoalDto goalDto) {
        val foundGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("No goal found by id " + goalId));
        goalMapper.updateGoalFromDto(goalDto, foundGoal);
        GoalUtil.updateTime(foundGoal, LocalDateTime.now());
        goalRepository.save(foundGoal);
        return foundGoal;
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
