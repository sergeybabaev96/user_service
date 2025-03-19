package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userId}/goal")
    public GoalViewDto createGoal(@PathVariable Long userId, @RequestBody GoalCreateDto goal) {
        log.info("Cоздание цели для пользователя {}", userId);
        return goalService.createGoal(userId, goal);
    }

    @PutMapping("/goal/{goalId}")
    public GoalViewDto updateGoal(@PathVariable Long goalId, @RequestBody GoalCreateDto goal) {
        log.info("Обновление цели{}", goalId);
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping("/goal/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        log.info("Удаление цели{}", goalId);
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/{goalId}/subGoals")
    public List<GoalViewDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filter) {
        log.info("Поиск отфильтрованных подзадач по задаче {}", goalId);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/{userId}/goals")
    public List<GoalViewDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        log.info("Поиск задач пользователя по фильтру {}", userId);
        return goalService.getGoalsByUser(userId, filter);
    }
}
