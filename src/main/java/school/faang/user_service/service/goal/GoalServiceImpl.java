package school.faang.user_service.service.goal;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.KafkaProduceException;
import school.faang.user_service.exception.MaxActiveGoalsLimitExceededException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.kafka.goal.GoalCompletedEvent;
import school.faang.user_service.kafka.goal.KafkaProducer;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.enums.goal.GoalEventType.GOAL_COMPLETED;
import static school.faang.user_service.enums.goal.GoalStatus.COMPLETED;

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
    private final KafkaProducer<GoalCompletedEvent> kafkaProducer;

    @Transactional
    @Override
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        checkActiveGoals(user);
        checkExistsSkills(goalDto.getSkillIds());
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParent());
        goalRepository.assignGoalToUser(goal.getId(), user.getId());
        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(goal.getId(), skillId));
        return goalMapper.toDto(goal);
    }

    @Transactional
    @Override
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal with id = %s not found", goalId)));
        if (COMPLETED.equals(goal.getStatus())) {
            throw new IllegalStateException(
                    String.format("Cannot update a completed goal with id = %d", goalId));
        }
        checkExistsSkills(goalDto.getSkillIds());
        goalRepository.removeSkillsFromGoal(goal.getId());
        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(goalId, skillId));

        if (COMPLETED.equals(goalDto.getStatus())) {
            List<User> users = userRepository.findUsersByGoalId(goal.getId());
            updateUserSkills(users, goalDto.getSkillIds());
            try {
                kafkaProducer.produce(
                        new GoalCompletedEvent(goalDto.getMentorId(), goalId, GOAL_COMPLETED, LocalDateTime.now()));
            } catch (JsonProcessingException e) {
                throw new KafkaProduceException(
                        String.format("Failed kafka produce goal completed event. Goal id = %d", goal.getId()));
            }
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
        for (Filter<Goal, GoalFilterDto> filter : goalFilters) {
            if (filter.isApplicable(inputFilters)) {
                subtasks = filter.apply(subtasks, inputFilters);
            }
        }
        return subtasks.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Override
    public List<GoalDto> findGoalsByUser(long userId, GoalFilterDto inputFilters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId);
        for (Filter<Goal, GoalFilterDto> filter : goalFilters) {
            if (filter.isApplicable(inputFilters)) {
                goals = filter.apply(goals, inputFilters);
            }
        }
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
            throw new MaxActiveGoalsLimitExceededException(
                    String.format("User with id = %d already has exists max count active goals", user.getId()));
        }
    }

    private User getMentorGoal(GoalDto dto) {
        return Optional.ofNullable(dto.getMentorId())
                .flatMap(userRepository::findById)
                .orElse(null);
    }

    private Goal getParentGoal(GoalDto dto) {
        Goal parentGoal = null;
        if (dto.getParent() != null) {
            parentGoal = goalRepository.findById(dto.getParent()).orElse(null);
        }
        return parentGoal;
    }
}
