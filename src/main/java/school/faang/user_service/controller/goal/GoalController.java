package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalCreateDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.GoalViewDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GoalController {
    private final GoalService goalService;

    public GoalViewDto createGoal(Long userId, GoalCreateDto goal) {
        log.info("Cоздание цели для пользователя {}", userId);
        return goalService.createGoal(userId, goal);
    }

    public GoalViewDto updateGoal(Long goalId, GoalCreateDto goal) {
        log.info("Обновление цели{}", goalId);
        return goalService.updateGoal(goalId, goal);
    }

    public void deleteGoal(Long goalId) {
        log.info("Удаление цели{}", goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalViewDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filter) {
        log.info("Поиск отфильтрованных подзадач по задаче {}", goalId);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalViewDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        log.info("Поиск задач пользователя по фильтру {}", userId);
        return goalService.getGoalsByUser(userId, filter);
    }
}
