package school.faang.user_service.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create-goal")
    public @ResponseBody ResponseEntity<GoalDto> createGoal(@RequestParam Long userId,
                                                            @RequestBody @NonNull Goal goal) {
        GoalDto createdGoal = goalService.createGoal(userId, goal);
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    @PutMapping("/update-goal")
    public @ResponseBody ResponseEntity<GoalDto> updateGoal(@RequestParam Long goalId,
                                                            @RequestBody @NonNull GoalDto goal) {
        GoalDto updatedGoal = goalService.updateGoal(goalId, goal);
        return new ResponseEntity<>(updatedGoal, HttpStatus.OK);
    }

    @DeleteMapping("/delete-goal")
    public @ResponseBody ResponseEntity<GoalDto> deleteGoal(@RequestParam long goalId) {
        GoalDto deletedGoal = goalService.deleteGoal(goalId);
        return new ResponseEntity<>(deletedGoal, HttpStatus.OK);
    }

    @GetMapping("/find-subtasks/{goalId}")
    public @ResponseBody ResponseEntity<List<GoalDto>>
    findSubtasksByGoalId(@PathVariable long goalId, @NonNull SearchGoalDto searchGoalDto) {
        List<GoalDto> subtask = goalService.findSubtasksByGoalId(goalId, searchGoalDto);
        return new ResponseEntity<>(subtask, HttpStatus.OK);
    }

    @GetMapping("/find-goals-by-user/{userId}")
    public @ResponseBody ResponseEntity<List<GoalDto>>
    getGoalsByUser(@PathVariable long userId, @NonNull SearchGoalDto searchGoalDto) {
        List<GoalDto> subtask = goalService.getGoalsByUser(userId, searchGoalDto);
        return new ResponseEntity<>(subtask, HttpStatus.OK);
    }
}
