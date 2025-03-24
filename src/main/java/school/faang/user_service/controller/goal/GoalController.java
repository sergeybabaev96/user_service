package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * @author juzu400
 * @see GoalViewDto
 * @see GoalCreateDto
 * @see GoalService
 * @see GoalFilterDto
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
@Tag(name = "Goal Management", description = "APIs for managing goals and subgoals")
public class GoalController {
    private final GoalService goalService;

    /**
     * Создание цели для заданного пользователя
     *
     * @param userId Идентификатор пользователя
     * @param goal DTO для создания цели
     * @return созданная цель
     */
    @Operation(
            summary = "Create a new goal",
            description = "Creates a new goal for the specified user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Goal created successfully",
                            content = @Content(schema = @Schema(implementation = GoalViewDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping("/{userId}")
    public GoalViewDto createGoal(@PathVariable Long userId, @RequestBody GoalCreateDto goal) {
        return goalService.createGoal(userId, goal);
    }

    /**
     * Обновление цели по её идентификатору
     *
     * @param goalId Идентификатор цели
     * @param goal DTO с новыми значениями цели
     * @return обновленная цель
     */
    @Operation(
            summary = "Update an existing goal",
            description = "Updates the goal with the specified ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Goal updated successfully",
                            content = @Content(schema = @Schema(implementation = GoalViewDto.class))),
                    @ApiResponse(responseCode = "404", description = "Goal not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PutMapping("/{goalId}")
    public GoalViewDto updateGoal(@PathVariable Long goalId, @RequestBody GoalCreateDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    /**
     * Удаление цели по её идентификатору
     *
     * @param goalId Идентификатор цели
     */
    @Operation(
            summary = "Delete a goal",
            description = "Deletes the goal with the specified ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Goal deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Goal not found")
            }
    )
    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    /**
     * Получение списка подцелей для цели с применением фильтра
     *
     * @param goalId Идентификатор цели-родителя
     * @param filter Фильтр для поиска
     * @return Список подцелей цели по фильтру
     */
    @Operation(
            summary = "Get subgoals by goal ID",
            description = "Returns a list of subgoals for the specified goal ID with optional filtering",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subgoals retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GoalViewDto.class)))),
                    @ApiResponse(responseCode = "404", description = "Parent goal not found")
            }
    )
    @GetMapping("/{goalId}/subGoals")
    public List<GoalViewDto> findSubtasksByGoalId(@PathVariable Long goalId, @ModelAttribute GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    /**
     * Получение списка целей пользователя с применением фильтра
     *
     * @param userId Идентификатор пользователя
     * @param filter Фильтр для поиска
     * @return Список целей пользователя по фильтру
     */
    @Operation(
            summary = "Get goals by user ID",
            description = "Returns a list of goals for the specified user ID with optional filtering",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Goals retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GoalViewDto.class)))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{userId}")
    public List<GoalViewDto> getGoalsByUser(@PathVariable Long userId, @ModelAttribute GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}
