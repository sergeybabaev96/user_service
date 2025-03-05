package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        GoalInvitationDto createdDto = service.createInvitation(goalInvitationDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

}
