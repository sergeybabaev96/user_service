package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEvent;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private static final int MAX_ACTIVE_GOALS_FOR_USER = 3;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<Filter<Goal, GoalFilterDto>> goalFilters;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Transactional
    @Override
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        checkActiveGoals(user);
        checkExistsSkills(goalDto.skillIds());
        Goal goal = goalRepository.create(goalDto.title(), goalDto.description(), goalDto.parent());
        goalRepository.assignGoalToUser(goal.getId(), user.getId());
        goalDto.skillIds().forEach(skillId -> goalRepository.addSkillToGoal(goal.getId(), skillId));
        return goalMapper.toDto(goal);
    }

    @Transactional
    @Override
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal with id = %s not found", goalId)));
        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new IllegalStateException("Cannot update a completed goal");
        }
        checkExistsSkills(goalDto.skillIds());
        goalRepository.removeSkillsFromGoal(goal.getId());
        goalDto.skillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));

        if (GoalStatus.COMPLETED.equals(goalDto.status())) {
            List<User> users = goalRepository.findUsersByGoalId(goal.getId());
            updateUserSkills(users, goalDto.skillIds());
            goalCompletedEventPublisher.publish(new GoalCompletedEvent(goalDto.mentorId(), goalId, LocalDateTime.now()));
        }
        Goal updatedGoal = goalRepository.save(getUpdateGoal(goalDto, goal));
        return goalMapper.toDto(updatedGoal);
    }

    @Override
    public void deleteGoalById(long id) {
        List<Goal> subtasks = goalRepository.findByParent(id);
        if (!subtasks.isEmpty()) {
            subtasks.forEach(goalRepository::delete);
        }
        goalRepository.deleteById(id);
    }

    @Override
    public List<GoalDto> findSubgoalsByGoalId(long id, GoalFilterDto inputFilters) {
        Goal goal = goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal with id = %d not found", id)));
        List<Goal> subtasks = goalRepository.findByParent(goal.getId());
        subtasks = filteredGoals(subtasks, inputFilters);
        return subtasks.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Override
    public List<GoalDto> findGoalsByUser(long userId, GoalFilterDto inputFilters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId);
        goals = filteredGoals(goals, inputFilters);
        return goals.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    private void updateUserSkills(List<User> users, List<Long> skillsIds) {
        List<Skill> skills = skillRepository.findAllById(skillsIds);
        users.forEach(user -> {
            user.getSkills().addAll(skills);
            userRepository.save(user);
        });
    }

    private void checkExistsSkills(List<Long> skillIds) {
        skillIds.forEach(id -> {
            if (!skillRepository.existsById(id)) {
                throw new EntityNotFoundException(
                        String.format("Skill with id = %d not found", id));
            }
        });
    }

    private Goal getUpdateGoal(GoalDto goalDto, Goal goal) {
        Goal updateGoal = goalMapper.update(goalDto, goal);
        updateGoal.setParent(getParentGoal(goalDto));
        updateGoal.setMentor(getMentorGoal(goalDto));
        return updateGoal;
    }

    private void checkActiveGoals(User user) {
        if (goalRepository.countActiveGoalsPerUser(user.getId()) >= MAX_ACTIVE_GOALS_FOR_USER) {
            log.error("User with id = {} can't have more than {} active goals", user.getId(), MAX_ACTIVE_GOALS_FOR_USER);
            throw new IllegalArgumentException(
                    String.format("User with id = %d already has exists max count active goals", user.getId()));
        }
    }

    private User getMentorGoal(GoalDto dto) {
        User mentor = null;
        if (dto.mentorId() != null) {
            mentor = userRepository.findById(dto.mentorId()).orElse(null);
        }
        return mentor;
    }

    private Goal getParentGoal(GoalDto dto) {
        Goal parentGoal = null;
        if (dto.parent() != null) {
            parentGoal = goalRepository.findById(dto.parent()).orElse(null);
        }
        return parentGoal;
    }

    private List<Goal> filteredGoals(List<Goal> goals, GoalFilterDto inputFilters) {
        for (Filter<Goal, GoalFilterDto> filter : goalFilters) {
            if (filter.isApplicable(inputFilters)) {
                goals = filter.apply(goals, inputFilters);
            }
        }
        return goals;
    }

}
