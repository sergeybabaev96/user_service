package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalNotExistException;
import school.faang.user_service.exception.goal.UserNotGoalOwnerException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.goal.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    private final UserContext userContext;

    private final GoalValidator goalValidator;
    private final SkillValidator skillValidator;

    private final SkillService skillService;

    @Transactional
    public Goal createGoal(Goal newGoalData, List<Long> skillsId, Long parentId) {
        long userId = userContext.getUserId();
        goalValidator.validateMaxActiveGoalLimit(goalRepository.countActiveGoalsPerUser(userId));

        if (parentId != null && !goalRepository.existsById(parentId)) {
            throw new GoalNotExistException(parentId);
        }

        Goal newGoal = goalRepository.create(newGoalData.getTitle(), newGoalData.getDescription(), parentId);
        goalRepository.assignGoalToUser(userId, newGoal.getId());

        if (!skillsId.isEmpty()) {
            skillService.assignSkillToGoal(newGoal.getId(), skillsId);
        }
        return newGoal;
    }

    @Transactional
    public Goal update(long goalId, Goal newGoalData, List<Long> skillsId) {
        Goal dbGoal = getGoalById(goalId);
        goalValidator.validateUpdateCompleteGoal(dbGoal);
        skillValidator.validateExistingSkills(
                skillsId.stream()
                        .filter(skillId -> !skillRepository.existsById(skillId))
                        .toList()
        );

        dbGoal.setTitle(newGoalData.getTitle());
        dbGoal.setDescription(newGoalData.getDescription());
        dbGoal.setStatus(newGoalData.getStatus());

        boolean skillsChanged = !dbGoal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .toList()
                .equals(skillsId);

        if (skillsChanged) {
            skillService.updateSkillForGoal(goalId, skillsId);
            dbGoal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goalId));
        }

        if (dbGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> involvedUsersId = goalRepository.findUsersByGoalId(dbGoal.getId());

            goalValidator.validateAllSubGoalsCompleted(goalId, goalRepository.findByParent(goalId));

            involvedUsersId.forEach(userId ->
                    skillService.assignSkillsToUser(userId, dbGoal.getSkillsToAchieve()));
        }

        goalRepository.save(dbGoal);

        return dbGoal;
    }

    @Transactional
    public void delete(long goalId) {
        Goal goal = getGoalById(goalId);

        boolean userNotOwner = goalRepository.findGoalsByUserId(userContext.getUserId())
                .noneMatch(userGoal -> userGoal.getId().equals(goalId));

        if(userNotOwner) {
            throw new UserNotGoalOwnerException(userContext.getUserId(), goalId);
        }

        goalRepository.removeGoalFromUser(userContext.getUserId(), goalId);

        if (goalRepository.findUsersByGoalId(goalId).isEmpty()) {
            goalRepository.findByParent(goalId).forEach(goalRepository::delete);
            goalRepository.delete(goal);
        }
    }

    public Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new GoalNotExistException(goalId));
    }
}