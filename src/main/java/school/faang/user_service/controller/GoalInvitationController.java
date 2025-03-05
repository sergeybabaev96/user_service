package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@RestController
@RequestMapping("/goal-invitations")
@AllArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService service;


    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        service.createInvitation(goalInvitationDto);

        return ResponseEntity.ok().build();
    }

}
