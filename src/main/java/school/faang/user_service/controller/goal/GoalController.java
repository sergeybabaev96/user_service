package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.request.GoalRequest;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalDto> createGoal(@RequestBody GoalRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(goalService.createGoal(request.getUserId(), request.getGoal()));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalDto> updateGoal(@RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(request.getUserId(), request.getGoal()));
    }

    @DeleteMapping(value = "/{goalId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoalDto>> getGoalsByUser(@PathVariable long userId, @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userId, filters));
    }

    @PutMapping("user/{userId}/goal/{goalId}")
    public ResponseEntity<GoalDto> completeTheGoal(@PathVariable @Min(1) long userId,
                                                   @PathVariable @Min(1) long goalId) {
        return ResponseEntity.ok(goalService.completeTheGoal(userId, goalId));
    }
}
