package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.goal.GoalInvitationConfig;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationConfig goalInvitationConfig;

    public void createInvitation(GoalInvitationDto invitationDto) {
        log.info("Creating invitation: {}", invitationDto);

        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);
        validateInvitation(invitation);

        invitation.setStatus(RequestStatus.PENDING);
        goalInvitationRepository.save(invitation);

        log.info("Invitation created successfully: {}", invitation);
    }

    public void acceptGoalInvitation(Long id) {
        log.info("Accepting invitation with ID: {}", id);

        GoalInvitation invitation = findInvitationById(id);
        validateAcceptance(invitation);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);

        log.info("Invitation accepted successfully: {}", invitation);
    }

    public void rejectGoalInvitation(Long id) {
        log.info("Rejecting invitation with ID: {}", id);

        GoalInvitation invitation = findInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);

        log.info("Invitation rejected successfully: {}", invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        log.info("Fetching invitations with filter: {}", filter);

        List<GoalInvitation> invitations = goalInvitationRepository.findByInviterIdAndInvitedIdAndStatus(
                filter.getInviterId(),
                filter.getInvitedId(),
                filter.getStatus()
        );

        log.info("Found {} invitations", invitations.size());
        return invitations.stream()
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateInvitation(GoalInvitation invitation) {
        if (invitation.getInviter() == null || invitation.getInvited() == null) {
            throw new InvalidInvitationException("Inviter and invited user must be specified.");
        }

        if (invitation.getInviter().getId().equals(invitation.getInvited().getId())) {
            throw new InvalidInvitationException("Inviter and invited user cannot be the same.");
        }

        userService.validateUserExists(invitation.getInviter().getId());
        userService.validateUserExists(invitation.getInvited().getId());

        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            throw new InvalidInvitationException("Goal does not exist.");
        }
    }

    private void validateAcceptance(GoalInvitation invitation) {
        if (goalRepository.countActiveGoalsPerUser(invitation.getInvited().getId()) >=
                goalInvitationConfig.getMaxActiveGoals()) {
            throw new InvalidInvitationException("User has reached the maximum number of active goals.");
        }

        if (goalInvitationRepository.existsByInvitedAndGoal(invitation.getInvited(), invitation.getGoal())) {
            throw new InvalidInvitationException("User is already working on this goal.");
        }
    }

    private GoalInvitation findInvitationById(Long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new InvalidInvitationException("Invitation not found."));
    }
}