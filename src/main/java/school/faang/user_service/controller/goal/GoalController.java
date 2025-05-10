package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.GoalDto;
import school.faang.user_service.entity.goal.mapper.GoalMapper;
import school.faang.user_service.service.GoalService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final CreateGoalMapperImpl createGoalMapper;
    private final UpdateGoalMapperImpl updateGoalMapper;

    @PostMapping
    public ResponseEntity<GoalDto> createGoal(@RequestBody @Valid GoalDto goalDto) {
        Goal createdGoal = goalService.createGoal(
                goalMapper.toGoal(goalDto),
                goalDto.skillsId(),
                goalDto.parentId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdGoal.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(goalMapper.toGoalDto(createdGoal));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(@PathVariable long goalId, @RequestBody @Valid GoalDto goalDto) {
        Goal createdGoal = goalService.update(
                goalId,
                goalMapper.toGoal(goalDto),
                goalDto.skillsId()
        );
        return ResponseEntity.ok(goalMapper.toGoalDto(createdGoal));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable long goalId) {
        goalService.delete(goalId);
        return ResponseEntity.ok().build();
    }
}