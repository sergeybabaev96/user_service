package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

import java.util.ArrayList;
import java.util.Collections;
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

    @Transactional
    public void createGoal(Long userId, GoalDto goal) {
        validateByExistsUserOnId(userId);
        validateByCountGoals(userId);
        validateByExistsGoalSkills(goal);
        Goal goalEntity = goalMapper.goalDtoToGoal(goal, getSkillsByIds(goal.skillIds()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (goalEntity.getUsers() == null) {
            goalEntity.setUsers(new ArrayList<>());
        }
        goalEntity.getUsers().add(user);
        goalEntity.setSkillsToAchieve(skillRepository.findAllById(goal.skillIds()));
        goalRepository.save(goalEntity);
        log.info("User {} accepted new goal {}", userId, goalEntity.getTitle());
    }

    public void deleteGoal(Long goalId) {
        validateByExistsGoalOnId(goalId);
        goalRepository.deleteById(goalId);
        log.info("Goal with id: {} was deleted", goalId);
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found"));
        validateByCompletionStatus(existingGoal);
        validateByExistsGoalSkills(goal);
        existingGoal.setStatus(goal.status());
        existingGoal.setSkillsToAchieve(skillRepository.findAllById(goal.skillIds()));
        goalRepository.save(existingGoal);
        log.info("{} goal updated", goalId);

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            List<Long> usersIds = goalRepository.findUserIdsByGoalId(goalId);
            List<Skill> skills = existingGoal.getSkillsToAchieve();
            List<User> users = userRepository.findAllById(usersIds);
            skills.forEach(skill -> {
                skill.setUsers(users);
                skillRepository.save(skill);
            });
        }
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId, SearchGoalDto searchGoalDto) {
        validateByExistsGoalOnId(goalId);
        return goalMapper.goalListToGoalDtoList(applyFiltersOnGoals(
                goalRepository.findByParent(goalId), searchGoalDto)
                .toList());
    }

    @Transactional
    public List<GoalDto> getGoalsByUserId(Long userId, SearchGoalDto searchGoalDto) {
        validateByExistsUserOnId(userId);
        return goalMapper.goalListToGoalDtoList(applyFiltersOnGoals(
                goalRepository.findGoalsByUserId(userId), searchGoalDto)
                .toList());
    }

    public Optional<Goal> findById(Long id) {
        return goalRepository.findById(id);
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

    private void validateByCompletionStatus(Goal goal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new GoalAlreadyCompletedException("Goal is already completed");
        }
    }

    private void validateByExistsGoalOnId(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal not found");
        }
    }

    private void validateByExistsGoalSkills(GoalDto goalDto) {
        List<Long> idsSkillsOnRepository = skillRepository.findAllById(goalDto.skillIds()).stream()
                .map(Skill::getId)
                .toList();
        if (!idsSkillsOnRepository.equals(goalDto.skillIds())) {
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
