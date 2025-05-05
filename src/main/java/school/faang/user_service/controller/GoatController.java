package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.service.GoatService;

// TODO: чем отличается от @Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoatController {
    private final GoatService goatService;

    // TODO: возможно стоит возвращать сущность после создания
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGoal(@RequestParam Long userId, @RequestBody GoalDto goalDto) {
        goatService.createGoal(userId, goalDto);
    }
}
