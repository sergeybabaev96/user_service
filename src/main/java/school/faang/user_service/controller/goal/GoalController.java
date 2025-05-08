package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
@Slf4j
public class GoalController {
    private final GoalService goalService;

    // TODO: возможно стоит возвращать сущность после создания
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createGoal(@RequestParam Long userId, @RequestBody @Valid GoalRequestDto goalRequestDto) {
        log.debug("Goal controller accepted request create goal {}", goalRequestDto);
        goalService.createGoal(userId, goalRequestDto);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public GoalResponseDto updateGoal(@RequestParam long goalId, @Valid @RequestBody GoalRequestDto goalRequestDto) {
        log.debug("Goal controller accepted request update goal with id {}", goalId);
        return goalService.updateGoal(goalId, goalRequestDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@RequestParam long goalId) {
        log.debug("Goal controller accepted request delete goal with id {}", goalId);
        goalService.deleteGoalById(goalId);
    }

    @GetMapping("/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalResponseDto> getSubtasksByGoalId(@RequestParam long goalParentId) {
        log.debug("Goal controller accepted request get subtasks with parent id {}", goalParentId);
        return goalService.getSubtasksByParentGoalId(goalParentId);
    }

    // TODO: пока что не понятно что за фильтр
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalResponseDto> getGoalsByUser(@RequestParam long userId) {
        log.debug("Goal controller accepted request get goats for user with id {}", userId);
        return goalService.getGoalsByUser(userId, new GoalFilterDto());
    }
}
