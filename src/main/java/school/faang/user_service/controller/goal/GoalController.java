package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    // TODO: возможно стоит возвращать сущность после создания
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGoal(@RequestParam Long userId, @RequestBody @Valid GoalDto goalDto) {
        goalService.createGoal(userId, goalDto);
    }
}
