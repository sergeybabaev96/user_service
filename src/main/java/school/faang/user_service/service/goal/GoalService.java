package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.skill.SkillValidation;
import school.faang.user_service.validation.user.UserValidation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final GoalValidation goalValidation;
    private final UserValidation userValidation;
    private final SkillValidation skillValidation;
    private final List<GoalFilter> goalFilters;

    public void createGoal(Long userId, Goal goal) {
        userValidation.validateByExistsUserOnId(userId);
        goalValidation.validateByCountGoals(userId);
        skillValidation.validateByExistsGoalSkills(goal);
        goalRepository.save(goal);
        log.info("User {} accepted new goal {}", userId, goal.getTitle());
    }

    public void deleteGoal(Long goalId) {
        goalValidation.validateByExistsGoalOnId(goalId);
        goalRepository.deleteById(goalId);
        log.info("{} goal deleted", goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        goalValidation.validateByExistsGoalOnId(goalId);
        Optional<Goal> goalOnId = goalRepository.findById(goalId);
        goalValidation.validateByExistsGoal(goalOnId);
        goalValidation.validateByCompletionStatus(goalOnId);
        Goal goalEntity = goalMapper.goalDtoToGoal(goal);
        skillValidation.validateByExistsGoalSkills(goalEntity);
        goalRepository.save(goalEntity);
        log.info("{} goal updated", goalId);
        if (goalEntity.getStatus() == GoalStatus.COMPLETED) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            achieveSkillsByUsers(users, goalEntity);
        }
    }

    private void achieveSkillsByUsers(List<User> users, Goal goal) {
        users.forEach(user -> {
            List<Skill> skills = user.getSkills();
            skills.addAll(goal.getSkillsToAchieve());
            user.setSkills(skills);
            userRepository.save(user);
            log.info("User {} achieved new skills", user.getId());
        });
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, SearchGoalDto searchGoalDto) {
        goalValidation.validateByExistsGoalOnId(goalId);
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        Stream<Goal> filteredGoals = applyFiltersOnGoals(goals, searchGoalDto);
        return goalMapper.goalListToGoalDtoList(filteredGoals.toList());
    }

    public List<GoalDto> getGoalsByUserId(Long userId, SearchGoalDto searchGoalDto) {
        userValidation.validateByExistsUserOnId(userId);
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        Stream<Goal> filteredGoals = applyFiltersOnGoals(goals, searchGoalDto);
        return goalMapper.goalListToGoalDtoList(filteredGoals.toList());
    }

    private Stream<Goal> applyFiltersOnGoals(Stream<Goal> goals, SearchGoalDto searchGoalDto) {
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(searchGoalDto)) {
                goals = goalFilter.apply(goals, searchGoalDto);
            }
        }
        log.info("Success applying filters");
        return goals;
    }
}
