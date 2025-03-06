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
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goalvalidator.GoalValidator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final long ACTIVE_GOALS_COUNT = 3;
    private final GoalValidator goalValidator;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public GoalDto createGoal(Long userId, Goal goal) {
        validateId(userId);
        goalValidator.validateTitle(goal);
        goalValidator.validateSkillsToAchieve(goal);
        if (goalRepository.countActiveGoalsPerUser(userId) == ACTIVE_GOALS_COUNT) {
            log.error("User can has only {} active goals.", ACTIVE_GOALS_COUNT, new IllegalStateException());
        }
        checkSkillsExistByUserId(userId, goal);
        Long parentId = goal.getParent().getId() != null ? goal.getParent().getId() : 0L;
        goalRepository.create(goal.getTitle(), goal.getDescription(), parentId);
        saveSkills(userId, goal);
        return goalMapper.toDto(goal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        validateId(goalId);
        if (goalDto.getTitle().isBlank()) {
            log.error("Title can not be null or empty.", new IllegalArgumentException());
        }
        Goal goalById = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found."));
        if (goalById.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new RuntimeException("The goal status should not be completed during the update.");
        }
        Goal goal = goalMapper.toEntity(goalDto);
        goalValidator.validateSkillsToAchieve(goal);
        checkSkillsExistByGoalId(goalId, goal);
        if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillsToAchieveIds = goalDto.getSkillIds();
            List<Skill> skillsToAchieve = skillService.findAllSkillsById(skillsToAchieveIds);
            List<User> usersByGoalId = goalRepository.findUsersByGoalId(goalId);
            usersByGoalId.forEach(user -> user.getSkills().addAll(new HashSet<>(skillsToAchieve)));
        }
        goalById.setTitle(goal.getTitle());
        goalById.setStatus(goalDto.getStatus());
        updateSkills(goalById, goalDto);
        goalById.setUpdatedAt(LocalDateTime.now());
        return goalMapper.toDto(goalById);
    }

    public GoalDto deleteGoal(long goalId) {
        Goal goalById = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found."));
        goalRepository.deleteById(goalId);
        return goalMapper.toDto(goalById);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, SearchGoalDto searchGoalDto) {
        Stream<Goal> subTasks = Optional.ofNullable(goalRepository.findByParent(goalId))
                .orElseThrow(() -> new IllegalArgumentException("Subtasks not found."));
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(searchGoalDto)) {
                goalFilter.apply(subTasks, searchGoalDto);
            }
        }
        return subTasks.map(goalMapper::toDto).toList();
    }

    public List<GoalDto> getGoalsByUser(long userId, SearchGoalDto searchGoalDto) {
        Stream<Goal> goalsByUserId = Optional.ofNullable(goalRepository.findGoalsByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("Goals by user id not found."));
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(searchGoalDto)) {
                goalFilter.apply(goalsByUserId, searchGoalDto);
            }
        }
        return goalsByUserId.map(goalMapper::toDto).toList();
    }

    private void validateId(Long id) {
        if (Objects.isNull(id)) {
            log.error("Id can not be null.", new IllegalArgumentException());
        }
    }

    private void checkSkillsExistByUserId(Long userId, Goal goal) {
        List<Skill> goalSkills = goal.getSkillsToAchieve();
        List<Skill> existingSkills = Optional.ofNullable(skillService.findSkillsByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("Skills not found for this user id."));
        goalValidator.validateSkillsExistInBase(goalSkills, existingSkills);
    }

    private void saveSkills(Long userId, Goal goal) {
        List<Skill> newSkills = goal.getSkillsToAchieve();
        skillService.saveAllSkills(newSkills);
        User userFromBase = userService.findUserById(userId);
        userFromBase.getGoals().add(goal);
        userFromBase.getSkills().addAll(new HashSet<>(newSkills));
    }

    private void checkSkillsExistByGoalId(Long goalId, Goal goal) {
        List<Skill> goalSkills = goal.getSkillsToAchieve();
        List<Skill> existingSkills = Optional.ofNullable(skillService.findSkillsByGoalId(goalId))
                .orElseThrow(() -> new IllegalArgumentException("Skills not found for this goal id."));
        goalValidator.validateSkillsExistInBase(goalSkills, existingSkills);
    }

    private void updateSkills(Goal goalFromBase, GoalDto goalDto) {
        goalFromBase.getSkillsToAchieve().clear();
        Goal mappedGoal = goalMapper.toEntity(goalDto);
        List<Skill> mappedGoalSkills = mappedGoal.getSkillsToAchieve();
        goalFromBase.getSkillsToAchieve().addAll(mappedGoalSkills);
    }
}
