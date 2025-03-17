package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/new")
    public void createGoal(@RequestParam Long userId, @RequestBody GoalDto goal) {
        validateByTitle(goal.title());
        goalService.createGoal(userId, goal);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PutMapping("/{goalId}")
    public void updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        validateByTitle(goal.title());
        goalService.updateGoal(goalId, goal);
    }

    @PostMapping("/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody SearchGoalDto searchGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, searchGoalDto);
    }

    @PostMapping("/user-goals")
    public List<GoalDto> getGoalsByUserId(@RequestParam Long userId, @RequestBody SearchGoalDto searchGoalDto) {
        return goalService.getGoalsByUserId(userId, searchGoalDto);
    }

    private void validateByTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Goal hasn't title");
        }
    }
}
