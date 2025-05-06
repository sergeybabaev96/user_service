package school.faang.user_service.service.goal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {
    private static final int MAX_NUM_ACTIVE_GOAL_FOR_USER = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;
    // TODO: возможно стоит переключиться на сервисы
    private final SkillRepository skillRepository;

    @Override
    public void createGoal(Long userId, final GoalRequestDto goalRequestDto) {
        Goal goalEntity = goalMapper.toGoalEntity(goalRequestDto);

        /*Создайте в классе GoalService метод createGoal для сохранения полученной цели.
        Перед тем, как сохранить цель в базу, нужно проверить,
        что количество активных целей пользователя не превышает максимального возможного числа активных целей.
        Сейчас это число - 3.
        Также нужно проверить, что цель содержит только существующие скиллы.
        Для этого нужно использовать SkillService и SkillRepository с их методами.

        Обратите внимание, что метод create сохраняет в базу цель без навыков.
        Чтобы сохранить навыки, нужно использовать отдельный метод.*/
        checkCountGoalForUser(userId);

        User userOwner = userService.getUserById(userId);
        List<User> users = new ArrayList<>();
        users.add(userOwner);
        goalEntity.setUsers(users);

        Goal savedGoal = goalRepository.save(goalEntity);
        log.info("Goal with id {} has been saved", savedGoal.getId());
    }

    @Override
    public void deleteGoalById(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal with id {} not found", goalId);
                    return new GoalNotFoundException(goalId);
                });
        goalRepository.delete(goal);
        log.info("Goal with id {} has been deleted", goalId);
    }

    private void checkCountGoalForUser(Long userId) {
        long countActiveGoalForUser = goalRepository.findGoalsByUserId(userId)
                .filter(goal -> Objects.equals(goal.getStatus(), ACTIVE))
                .count();

        log.debug("count active goal for user with id {} {}", userId, countActiveGoalForUser);

        if (countActiveGoalForUser > MAX_NUM_ACTIVE_GOAL_FOR_USER) {
            log.error("Count active goal more max, max goal {}", MAX_NUM_ACTIVE_GOAL_FOR_USER);
            throw new CountActiveGoalMoreMaxException(MAX_NUM_ACTIVE_GOAL_FOR_USER);
        }
    }
}
