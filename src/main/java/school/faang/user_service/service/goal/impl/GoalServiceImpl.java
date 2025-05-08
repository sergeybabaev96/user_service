package school.faang.user_service.service.goal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.CreateGoalCompletedException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {
    private static final int MAX_NUM_ACTIVE_GOAL_FOR_USER = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final SkillService skillService;

    @Override
    public GoalResponseDto createGoal(Long userId, final GoalRequestDto goalRequestDto) {
        checkGoalBeforeInsert(userId, goalRequestDto);

        Goal goalEntity = goalMapper.toGoalEntity(goalRequestDto);

        User owner = userService.getUserById(userId);
        List<User> users = new ArrayList<>();
        users.add(owner);
        goalEntity.setUsers(users);

        Goal parentGoal = getGoalById(goalRequestDto.getParentId());
        goalEntity.setParent(parentGoal);

        Goal savedGoal = goalRepository.save(goalEntity);
        log.info("Goal with id {} has been saved", savedGoal.getId());

        return goalMapper.toGoalResponseDto(savedGoal);
    }

    @Override
    public GoalResponseDto updateGoal(long goalId, final GoalRequestDto goalRequestDto) {
        Goal goal = getGoalById(goalId);

        checkGoalBeforeUpdate(goal, goalRequestDto);

        goalMapper.update(goal, goalRequestDto);

        List<Skill> skills = goalRequestDto.getSkillIds().stream()
                .map(skillService::getSkillById)
                .collect(Collectors.toList());
        goal.setSkillsToAchieve(skills);

        Goal saveGoal = goalRepository.save(goal);
        log.info("Goal with id {} has been update", goalRequestDto);

        assignsSkillsToUser(saveGoal);

        return goalMapper.toGoalResponseDto(saveGoal);
    }

    @Override
    public void deleteGoalById(long goalId) {
        checkGoalBeforeDelete(goalId);

        goalRepository.deleteById(goalId);
        log.info("Goal with id {} has been deleted", goalId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponseDto> getSubtasksByParentGoalId(long goalParentId) {
        try (Stream<Goal> streamGoal = goalRepository.findByParent(goalParentId)) {
            return streamGoal
                    .map(goalMapper::toGoalResponseDto)
                    .toList();
        }
    }

    @Override
    public List<GoalResponseDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        GoalStatus statusFilter = filter.isCompleted() ? GoalStatus.COMPLETED : GoalStatus.ACTIVE;

        return goalRepository.findGoalsByUserId(userId)
                .filter(goal -> Objects.equals(goal.getTitle(), filter.getTitle()))
                .filter(goal -> Objects.equals(goal.getStatus(), statusFilter))
                .map(goalMapper::toGoalResponseDto)
                .toList();
    }

    @Override
    public Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal with id {} not found", goalId);
                    return new GoalNotFoundException(goalId);
                });
    }

    @Override
    public void checkGoalById(long goalId) {
        getGoalById(goalId);
    }

    private void checkGoalBeforeInsert(long userId, GoalRequestDto goalRequestDto) {
        if (goalRequestDto.isCompleted()) {
            String errorMsg = "Trying create the completed goal";
            log.error(errorMsg);
            throw new CreateGoalCompletedException(errorMsg);
        }
        checkCountGoalForUser(userId);

        checkSkills(goalRequestDto);
    }

    private void checkGoalBeforeUpdate(Goal goal, GoalRequestDto goalRequestDto) {
        if (Objects.equals(goal.getStatus(), GoalStatus.COMPLETED)) {
            String errorMsg = String.format("Trying change the completed goal with id %d", goal.getId());
            log.error(errorMsg);
            throw new GoalAlreadyCompletedException(errorMsg);
        }

        checkSkills(goalRequestDto);
    }

    private void checkGoalBeforeDelete(long goalId) {
        checkGoalById(goalId);
    }

    private void checkCountGoalForUser(Long userId) {
        int countActiveGoalForUser = goalRepository.countActiveGoalsPerUser(userId);

        log.debug("Count active goal for user with id {} {}", userId, countActiveGoalForUser);

        if (countActiveGoalForUser > MAX_NUM_ACTIVE_GOAL_FOR_USER) {
            log.error("Count active goal more max, max goal {}", MAX_NUM_ACTIVE_GOAL_FOR_USER);
            throw new CountActiveGoalMoreMaxException(MAX_NUM_ACTIVE_GOAL_FOR_USER);
        }
    }

    private void assignsSkillsToUser(Goal saveGoal) {
        if (Objects.equals(saveGoal.getStatus(), GoalStatus.COMPLETED)) {
            List<Long> userIds = saveGoal.getUsers().stream()
                    .map(User::getId)
                    .toList();
            List<Long> skillIds = saveGoal.getSkillsToAchieve().stream()
                    .map(Skill::getId)
                    .toList();
            skillService.assignsSkillsToUser(skillIds, userIds);
        }
    }

    private void checkSkills(GoalRequestDto goalRequestDto) {
        goalRequestDto.getSkillIds().forEach(skillService::checkSkillById);
    }
}
