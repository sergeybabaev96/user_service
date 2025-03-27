package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Goals API", description = "API для управления целями пользователя")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/new")
    @Operation(summary = "Создание цели", description = "Создает новую цель на основе переданных данных для пользователя")
    public void createGoal(@Parameter(description = "Идентификатор пользователя") @RequestParam Long userId,
                           @RequestBody GoalDto goal) {
        validateByTitle(goal.title());
        goalService.createGoal(userId, goal);
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "Удаление цели", description = "Удаляет цель из базы, на основе переданного идентификатора")
    public void deleteGoal(@Parameter(description = "Идентификатор цели") @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "Обновление цели",
            description = "Обновляет цель с переданным идентификатором, на основе переданных данных")
    public void updateGoal(@Parameter(description = "Идентификатор цели") @PathVariable Long goalId,
                           @RequestBody GoalDto goal) {
        validateByTitle(goal.title());
        goalService.updateGoal(goalId, goal);
    }

    @PostMapping("/{goalId}")
    @Operation(summary = "Найти подцели",
            description = "Находит подцели, на основе переданного идентификатора цели-родителя," +
                    " а также выводит их по переданному фильтру")
    public List<GoalDto> findSubtasksByGoalId(@Parameter(description = "Идентификатор цели")
                                                  @PathVariable Long goalId, @RequestBody SearchGoalDto searchGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, searchGoalDto);
    }

    @PostMapping("/user-goals")
    @Operation(summary = "Найти цели пользователя",
            description = "Находит все цели пользователя с переданным идентификатором, " +
                    "а также выводит их по переданному фильтру")
    public List<GoalDto> getGoalsByUserId(@Parameter(description = "Идентификатор пользователя")
                                              @RequestParam Long userId, @RequestBody SearchGoalDto searchGoalDto) {
        return goalService.getGoalsByUserId(userId, searchGoalDto);
    }

    private void validateByTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Goal hasn't title");
        }
    }
}
