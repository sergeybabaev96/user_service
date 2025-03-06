package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.Map;

@RestController
@RequestMapping("/goal-invitations")
@AllArgsConstructor
@Slf4j
public class GoalInvitationController {
    private final GoalInvitationService service;


    @PostMapping("/create")
    public ResponseEntity<?> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        try {
            GoalInvitationDto createdDto = service.createInvitation(goalInvitationDto);

            return ResponseEntity.ok(createdDto);
        } catch (Exception e) {
            log.error("Error creating request: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error creating request", "details", e.getMessage()));
        }
    }

    @GetMapping("/accept/{id}")
    public ResponseEntity<?> acceptGoalInvitation(@PathVariable long id) {
        try {
            service.acceptGoalInvitation(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error accepting invitation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error accepting invitation", "details", e.getMessage()));
        }
    }

}
