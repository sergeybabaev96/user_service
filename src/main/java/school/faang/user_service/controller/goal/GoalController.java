package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/goal")
public class GoalController {
    private static final String INVALID_ID_MSG = "can`t be less than 1";
    private final GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestParam @Min(value = 1, message = INVALID_ID_MSG) long userId,
                              @Valid @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable @Min(value = 1, message = INVALID_ID_MSG) long goalId,
                              @Valid @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable @Min(value = 1, message = INVALID_ID_MSG) long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> getSubtasksByGoalId(
            @PathVariable @Min(value = 1, message = INVALID_ID_MSG) long goalId, GoalFilterDto filter) {
        return goalService.getSubtasksByGoalId(goalId, filter);
    }

    @GetMapping
    public List<GoalDto> getGoalsByUserId(
            @RequestParam @Min(value = 1, message = INVALID_ID_MSG) long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUserId(userId, filter);
    }
}