package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalDtoValidator;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        GoalDtoValidator.validateGoalDto(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

}
