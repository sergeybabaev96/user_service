package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.UserNotFoundException;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoatService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoatServiceImpl implements GoatService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    // TODO: возможно стоит переключиться на сервисы
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Override
    public void createGoal(Long userId, final GoalDto goalDto) {
        Goal goal = goalMapper.toGoalEntity(goalDto);

        /*Создайте в классе GoalService метод createGoal для сохранения полученной цели.
        Перед тем, как сохранить цель в базу, нужно проверить,
        что количество активных целей пользователя не превышает максимального возможного числа активных целей.
        Сейчас это число - 3.
        Также нужно проверить, что цель содержит только существующие скиллы.
        Для этого нужно использовать SkillService и SkillRepository с их методами.

        Обратите внимание, что метод create сохраняет в базу цель без навыков.
        Чтобы сохранить навыки, нужно использовать отдельный метод.*/

        User userOwner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<User> users = new ArrayList<>();
        users.add(userOwner);
        goal.setUsers(users);

        goalRepository.save(goal);
    }
}
