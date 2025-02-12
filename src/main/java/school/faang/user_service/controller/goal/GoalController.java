package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Tag(name = "Цели")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/goals")
@Validated
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "Создать новую цель")
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@PathVariable @Min(1) long userId, @RequestBody GoalDto dto) {
        return goalService.createGoal(userId, dto);
    }

    @Operation(summary = "Обновить цель")
    @PutMapping("/{id}")
    public GoalDto updateGoal(@PathVariable @Min(1) long id, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(id, goalDto);
    }

    @Operation(summary = "Удалить цель")
    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable @Min(1) long id) {
        goalService.deleteGoalById(id);
    }

    @Operation(summary = "Получить все подзадачи цели по фильтру")
    @GetMapping("/{id}/subtasks")
    public List<GoalDto> getSubtasksByGoalId(@PathVariable @Min(1) long id, GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(id, filters);
    }

    @Operation(summary = "Получить список целей по фильтру")
    @GetMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable @Min(1) long userId, GoalFilterDto filters) {
        return goalService.findGoalsByUser(userId, filters);
    }
}
