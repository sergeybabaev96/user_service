package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalDtoValidator;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalDtoValidator goalDtoValidator;
    private final GoalService goalService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalDtoValidator.validateGoalDto(goalDto, userId);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalDtoValidator.validateGoalDto(goalDto, goalId);

        return goalService.updateGoal(goalId, goalDto);
    }
}