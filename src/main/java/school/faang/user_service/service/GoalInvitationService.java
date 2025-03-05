package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserSevice userSevice;
    private final GoalService goalService;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        validateFields(goalInvitationDto, goalInvitation);

        goalInvitationRepository.save(goalInvitation);
    }

    private void validateFields(GoalInvitationDto goalInvitationDto, GoalInvitation goalInvitation) {
        var goalIdId = Objects.requireNonNull(goalInvitationDto.getGoalId(), "Goal is required");
        var inviterId = Objects.requireNonNull(goalInvitationDto.getInviterId(), "InviterId is required");
        var invitedId = Objects.requireNonNull(goalInvitationDto.getInvitedId(), "InvitedId is required");

        if (inviterId.equals(invitedId)) {
            throw new IllegalArgumentException("Inviter and invited must not be same person");
        }

        var goal = goalService.findById(goalIdId)
                .orElseThrow(() -> new EntityNotFoundException("Goal doesn't exist"));
        var inviter = userSevice.findById(inviterId)
                        .orElseThrow(() -> new EntityNotFoundException("Inviter doesn't exist"));
        var invited = userSevice.findById(invitedId)
                        .orElseThrow(() -> new EntityNotFoundException("Invited doesn't exist"));

        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
    }
}
