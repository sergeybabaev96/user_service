package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.CountActiveGoalMoreMaxException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoatService;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class GoatServiceImpl implements GoatService {
    private static final int MAX_NUM_ACTIVE_GOAL_FOR_USER = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;
    // TODO: возможно стоит переключиться на сервисы
    private final SkillRepository skillRepository;

    @Override
    public void createGoal(Long userId, final GoalDto goalDto) {
        Goal goalEntity = goalMapper.toGoalEntity(goalDto);

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

        goalRepository.save(goalEntity);
    }

    private void checkCountGoalForUser(Long userId) {
        long countActiveGoalForUser = goalRepository.findGoalsByUserId(userId)
                .filter(goal -> Objects.equals(goal.getStatus(), ACTIVE))
                .count();

        if (countActiveGoalForUser > MAX_NUM_ACTIVE_GOAL_FOR_USER) {
            throw new CountActiveGoalMoreMaxException(MAX_NUM_ACTIVE_GOAL_FOR_USER);
        }
    }
}
