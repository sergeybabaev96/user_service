package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.CreateGoalDto;
import school.faang.user_service.entity.goal.dto.UpdateGoalDto;
import school.faang.user_service.entity.goal.mapper.CreateGoalMapperImpl;
import school.faang.user_service.entity.goal.mapper.UpdateGoalMapperImpl;
import school.faang.user_service.service.GoalService;

@RestController
@RequestMapping("/api/v1/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final CreateGoalMapperImpl createGoalMapper;
    private final UpdateGoalMapperImpl updateGoalMapper;

    @PostMapping("/create")
    public ResponseEntity<CreateGoalDto> createGoal(@Valid @RequestBody CreateGoalDto createGoalDto) {
        Goal createdGoal = goalService.createGoal(
                createGoalMapper.dtoToGoal(createGoalDto),
                createGoalDto.skillsId(),
                createGoalDto.parent()
        );
        return ResponseEntity.ok(createGoalMapper.goalToDto(createdGoal));
    }

    @PutMapping("/update/{goalId}")
    public ResponseEntity<UpdateGoalDto> updateGoal(@PathVariable long goalId, @Valid @RequestBody UpdateGoalDto updateGoalDto) {
        Goal createdGoal = goalService.update(
                goalId,
                updateGoalMapper.dtoToGoal(updateGoalDto),
                updateGoalDto.skillsId()
        );
        return ResponseEntity.ok(updateGoalMapper.goalToDto(createdGoal));
    }
}