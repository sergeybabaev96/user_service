package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.service.goal.GoalService;
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
@Slf4j
public class GoalController {
    private final GoalService goalService;

    // TODO: возможно стоит возвращать сущность после создания
    @PostMapping("/create/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGoal(@RequestParam Long userId, @RequestBody @Valid GoalRequestDto goalRequestDto) {
        log.debug("Goal controller accepted request create goal {}", goalRequestDto);
        goalService.createGoal(userId, goalRequestDto);
    }

    @DeleteMapping("/delete/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@RequestParam long goalId) {
        log.debug("Goal controller accepted request delete goal with id {}", goalId);
        goalService.deleteGoalById(goalId);
    }
}
