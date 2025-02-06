package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalRequestDto;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalRequestDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<CreateGoalResponse> createGoal(
            @Valid @RequestBody CreateGoalRequestDto createGoalRequest
    ) {
        CreateGoalResponse response = goalService.createGoal(createGoalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<UpdateGoalResponse> updateGoal(
            @Valid @RequestBody UpdateGoalRequestDto updateGoalRequest
    ) {
        UpdateGoalResponse response = goalService.updateGoal(updateGoalRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable long goalId
    ) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{goalId}/subtasks")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(
            @PathVariable long goalId,
            @ModelAttribute GoalFilterDto filter
    ) {
        List<GoalDto> subtasks = goalService.findSubtasksByGoalId(goalId, filter);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(
            @PathVariable Long userId,
            @ModelAttribute GoalFilterDto filter
    ) {
        List<GoalDto> goals = goalService.getGoalsByUser(userId, filter);
        return ResponseEntity.ok(goals);
    }
}