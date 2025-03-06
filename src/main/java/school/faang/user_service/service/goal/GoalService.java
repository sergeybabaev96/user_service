package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.CreateGoalRequest;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.dto.goal.UpdateGoalRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.exception.UserGoalLimitExceededException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.GoalFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class GoalService {
    private final static int USER_GOAL_MAX_COUNT = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filters;

    @Transactional
    public GoalResponse createGoal(long userId, CreateGoalRequest goalDto) {
        userValid(userId);

        if (goalRepository.countActiveGoalsPerUser(userId) >= USER_GOAL_MAX_COUNT) {
            throw new UserGoalLimitExceededException("пользователь может иметь не больше + " + USER_GOAL_MAX_COUNT + " целей");
        }

        List<Skill> skillsToGoal = getSkillsFromDto(goalDto);

        Goal goal = goalRepository.create(goalDto.title(), goalDto.description(), goalDto.parentId());
        goal.setSkillsToAchieve(skillsToGoal);
        goal = goalRepository.save(goal);

        return goalMapper.toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(UpdateGoalRequest goalDto) {
        Goal goal = getGoalById(goalDto.id());
        GoalStatus currentStatus = goal.getStatus();

        goalMapper.updateEntityFromDto(goalDto, goal);

        if (goal.getStatus() == GoalStatus.COMPLETED && currentStatus == GoalStatus.ACTIVE) {
            goal.getSkillsToAchieve().forEach(skill ->
                    skill.getUsers().forEach(user -> skillRepository.assignSkillToUser(skill.getId(), user.getId()))
            );
        }

        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    public void deleteGoal(long goalId) {
        Goal goal = getGoalById(goalId);
        goalRepository.delete(goal);
    }

    public List<GoalResponse> getGoals(long userId, GoalFilterDto filterDto) {
        userValid(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);
        return filterGoals(goalStream, filterDto)
                .map(g -> goalMapper.toResponse(g))
                .toList();
    }

    public List<GoalResponse> getSubtasksGoal(long goalId) {
        getGoalById(goalId);

        return goalRepository.findByParent(goalId)
                .map(g -> goalMapper.toResponse(g))
                .toList();
    }

    @Transactional
    public void removeUserFromGoal(Goal goal, long userId) {
        List<User> users = new ArrayList<>(goal.getUsers());
        if (!users.removeIf(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("Пользователь " + userId + " у цели не был найден");
        }
        goal.setUsers(users);
        if (goal.getUsers().isEmpty()) {
            deleteGoal(goal.getId());
        } else {
            goalRepository.save(goal);
        }
    }

    private Goal getGoalById(Long goalId) {
        if (goalId == null) {
            throw new IllegalArgumentException("Нет ID");
        }

        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Данной цели не существует"));
    }

    private void userValid(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не существует");
        }
    }

    private Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        for (GoalFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                goals = filter.apply(goals, filterDto);
            }
        }

        return goals;
    }

    private Skill getSkillById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        return skillRepository.findById(id).orElseThrow(
                () -> new SkillNotFoundException("Скилл с ID: " + id + " не существует"));
    }

    private List<Skill> getSkillsFromDto(CreateGoalRequest goalDto) {
        if (goalDto.skillsToAchieveIds() != null) {
            return goalDto.skillsToAchieveIds().stream()
                    .map(id -> getSkillById(id))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
