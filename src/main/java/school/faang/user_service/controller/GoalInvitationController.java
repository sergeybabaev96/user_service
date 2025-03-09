package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;


@RestController
@RequestMapping("/goal-invitations")
@AllArgsConstructor
@Slf4j
public class GoalInvitationController {
    private final GoalInvitationService service;


    @PostMapping("/create")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        return service.createInvitation(goalInvitationDto);
    }

    @PostMapping("/accept/{id}")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        return service.acceptGoalInvitation(id);
    }

    @PostMapping("/reject/{id}")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable long id) {
        return service.rejectGoalInvitation(id);
    }

    @PostMapping("/filter")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterIDto filter) {
        return service.getInvitations(filter);
    }

}
