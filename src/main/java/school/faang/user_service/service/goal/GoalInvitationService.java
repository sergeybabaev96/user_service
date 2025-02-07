package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.filter.goal.invitation.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.operations.GoalInvitationValidator;
import school.faang.user_service.service.goal.operations.StatusUpdater;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final StatusUpdater statusUpdater;
    private final List<InvitationFilter> invitationFilters;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);

        goalInvitationValidator.validate(invitationDto, invitation.getGoal());
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation savedInvitation = goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(Long invitationId) {
        GoalInvitation invitation = findInvitationById(invitationId);

        goalInvitationValidator.validate(invitation);
        statusUpdater.updateStatus(invitation, RequestStatus.ACCEPTED);

        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(Long invitationId) {
        GoalInvitation invitation = findInvitationById(invitationId);

        goalInvitationValidator.validate(invitation);
        statusUpdater.updateStatus(invitation, RequestStatus.REJECTED);

        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Stream<GoalInvitation> invitationsStream = goalInvitationRepository.findAll().stream();

        for (InvitationFilter invitationFilter : invitationFilters) {
            if (invitationFilter.isApplicable(filter)) {
                invitationsStream = invitationFilter.apply(invitationsStream, filter);
            }
        }

        return invitationsStream
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    private GoalInvitation findInvitationById(Long invitationId) {
        return goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvalidInvitationException("Invitation does not exist."));
    }
}