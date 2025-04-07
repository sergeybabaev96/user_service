package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

/**
 * Контроллер для работы с сущность <li>{@link Goal}
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с целями,
 * их создание, изменение, удаление и передачу в сервисный слой для выполнения операций с данными.
 * </p>
 *
 * <p><b>Основные функции:</b>
 * <ul>
 *     <li>{@link #createGoal(Long, GoalCreateDto) Создание новой цели} для заданного пользователя.</li>
 *     <li>{@link #updateGoal(Long, GoalCreateDto) Обновление существующей цели}.</li>
 *     <li>{@link #deleteGoal(Long) Удаление цели} по её идентификатору.</li>
 *     <li>{@link #findSubtasksByGoalId(Long, GoalFilterDto) Получение списка подцелей с применением фильтра}, по заданной цели.</li>
 *     <li>{@link #getGoalsByUser(Long, GoalFilterDto) Получение списка всех целей с применением фильтра}, для заданного пользователя.</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 * @see GoalViewDto
 * @see GoalCreateDto
 * @see GoalService
 * @see GoalFilterDto
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
@Tag(name = "Goal Management", description = "Provides methods for working with goals")
public class GoalController {
    private final GoalService goalService;

    @Operation(
            summary = "Create a new goal",
            description = "Creates a new goal for the specified user"
    )
    @PostMapping("/{userId}")
    public GoalViewDto createGoal(
            @Parameter(description = "ID of the user who owns the goal", required = true, example = "123")
            @PathVariable Long userId,
            @RequestBody GoalCreateDto goal) {
        return goalService.createGoal(userId, goal);
    }

    @Operation(
            summary = "Update an existing goal",
            description = "Updates the goal with the specified ID"
    )
    @PutMapping("/{goalId}")
    public GoalViewDto updateGoal(
            @Parameter(description = "ID of the goal to update", required = true, example = "1")
            @PathVariable Long goalId,
            @RequestBody GoalCreateDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    @Operation(
            summary = "Delete a goal",
            description = "Deletes the goal with the specified ID"
    )
    @DeleteMapping("/{goalId}")
    public void deleteGoal(
            @Parameter(description = "ID of the goal to delete", required = true, example = "1")
            @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @Operation(
            summary = "Get subgoals by goal ID",
            description = "Returns a list of subgoals for the specified goal ID with optional filtering"
    )
    @GetMapping("/{goalId}/subGoals")
    public List<GoalViewDto> findSubtasksByGoalId(
            @Parameter(description = "ID of the parent goal", required = true, example = "1")
            @PathVariable Long goalId,
            @ModelAttribute GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @Operation(
            summary = "Get goals by user ID",
            description = "Returns a list of goals for the specified user ID with optional filtering"
    )
    @GetMapping("/{userId}")
    public List<GoalViewDto> getGoalsByUser(
            @Parameter(description = "ID of the user to filter goals", required = true, example = "123")
            @PathVariable Long userId,
            @ModelAttribute GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}
