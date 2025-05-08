package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.goal.GoalDto;
import school.faang.user_service.service.GoalService;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/create")
    public ResponseEntity<GoalDto> createGoal(@RequestParam Long userId, @Valid @RequestBody GoalDto goalDto) {
        GoalDto createdGoal = goalService.createGoal(userId, goalDto);
        return ResponseEntity.ok(createdGoal);
    }
}