package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.skill.SkillValidation;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final GoalValidation goalValidation;
    private final SkillValidation skillValidation;

    public void createGoal(Long userId, Goal goal) {
        goalValidation.validateByCountGoals(userId);
        skillValidation.validateByExistsGoalSkills(goal);
        goalRepository.save(goal);
        log.info("User {} accepted new goal {}", userId, goal.getTitle());
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
        log.info("{} goal deleted", goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        Optional<Goal> goalOnId = goalRepository.findById(goalId);
        goalValidation.validateByExistsGoal(goalOnId);
        goalValidation.validateByCompletionStatus(goalOnId);
        Goal goalEntity = goalMapper.goalDtoToGoal(goal);
        skillValidation.validateByExistsGoalSkills(goalEntity);
        goalRepository.save(goalEntity);
        if (goalEntity.getStatus() == GoalStatus.COMPLETED) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            users.forEach(user -> {
                List<Skill> skills = user.getSkills();
                skills.addAll(goalEntity.getSkillsToAchieve());
                user.setSkills(skills);
                userRepository.save(user);
            });
        }
    }
}
