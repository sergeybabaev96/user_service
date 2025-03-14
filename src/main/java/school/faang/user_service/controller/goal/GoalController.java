package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userId}")
    public GoalDto createGoal(@PathVariable long userId, @RequestBody GoalDto goalDto) {
        validateStringField(goalDto.getTitle(), "title");
        validateStringField(goalDto.getDescription(), "description");
        primaryValidateId(goalDto.getParentId());

        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable long goalId, @RequestBody GoalDto goalDto) {
        validateStringField(goalDto.getTitle(), "title");
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> getSubtasksByGoalId(@PathVariable long goalId, @RequestBody GoalFilterDto filter) {
        validateGoalFilterDto(filter);
        return goalService.getSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/{userId}")
    public List<GoalDto> getGoalsByUserId(@PathVariable long userId, @RequestBody GoalFilterDto filter) {
        validateGoalFilterDto(filter);
        return goalService.getGoalsByUserId(userId, filter);
    }

    private void primaryValidateId(long id) {
        if (id < 0) {
            throw new DataValidationException("Invalid id: " + id);
        }
    }

    private void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new DataValidationException("Invalid " + fieldName + ": " + value);
        }
    }

    private void validateGoalFilterDto(GoalFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null.");
        }
    }
}