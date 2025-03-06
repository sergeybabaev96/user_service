package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.constants.goal.GoalConstants;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.skill.SkillLimitExceededException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public void createGoal(Long userId, Goal goal) {
        validateByExistsUserOnId(userId);
        validateByCountGoals(userId);
        validateByExistsGoalSkills(goal);
        goalRepository.save(goal);
        log.info("User {} accepted new goal {}", userId, goal.getTitle());
    }

    public void deleteGoal(Long goalId) {
        validateByExistsGoalOnId(goalId);
        goalRepository.deleteById(goalId);
        log.info("{} goal deleted", goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        validateByExistsGoalOnId(goalId);
        Optional<Goal> goalOnId = goalRepository.findById(goalId);
        validateByExistsGoal(goalOnId);
        validateByCompletionStatus(goalOnId);
        Goal goalEntity = goalMapper.goalDtoToGoal(goal, getSkillsByIds(goal.skillIds()));
        validateByExistsGoalSkills(goalEntity);
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
        validateByExistsGoalOnId(goalId);
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        Stream<Goal> filteredGoals = applyFiltersOnGoals(goals, searchGoalDto);
        return goalMapper.goalListToGoalDtoList(filteredGoals.toList());
    }

    public List<GoalDto> getGoalsByUserId(Long userId, SearchGoalDto searchGoalDto) {
        validateByExistsUserOnId(userId);
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

    private void validateByCountGoals(Long userId) {
        if (goalRepository.findGoalsByUserId(userId).count() == GoalConstants.MAX_COUNT_GOALS_PER_USER) {
            throw new SkillLimitExceededException(
                    "There can be no more than " + GoalConstants.MAX_COUNT_GOALS_PER_USER + " goals.");
        }
    }

    private void validateByExistsGoal(Optional<Goal> goal) {
        if (goal.isEmpty()) {
            throw new EntityNotFoundException("Goal not found");
        }
    }

    private void validateByCompletionStatus(Optional<Goal> goal) {
        if (goal.isPresent() && goal.get().getStatus() == GoalStatus.COMPLETED) {
            throw new GoalAlreadyCompletedException("Goal is already completed");
        }
    }

    private void validateByExistsGoalOnId(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal not found");
        }
    }

    private void validateByExistsGoalSkills(Goal goal) {
        boolean isContained = new HashSet<>(goal.getSkillsToAchieve()).containsAll(skillRepository.findAll());
        if (!isContained) {
            throw new IllegalArgumentException("Goal contains non-existent skills");
        }
    }

    private void validateByExistsUserOnId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
    }

    private List<Skill> getSkillsByIds(List<Long> skillIds) {
        if (skillIds == null) {
            return Collections.emptyList();
        }
        return skillIds.stream()
                .map(skillRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
