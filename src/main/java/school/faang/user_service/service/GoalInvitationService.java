package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validation.goal.GoalInvitationValidation;
import school.faang.user_service.filter.GoalInvitationFilter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidation validation;
    private final List<GoalInvitationFilter> invitationFilter;
    private final UserService userService;
    private final GoalService goalService;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto){
        validation.checkInvitation(dto);
        Goal goal = goalService.getGoalById(dto.getGoalId());
        User inviter = userService.getUserById(dto.getInviterId());
        User invited = userService.getUserById(dto.getInvitedUserId());

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(dto);
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);

        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id){
        validation.checkAcceptingInvitation(id);
        return decisionOnGoalInvitation(id, RequestStatus.ACCEPTED);
    }

    public GoalInvitationDto rejectGoalInvitation(Long id){
        validation.checkRejectingInvitation(id);
        return decisionOnGoalInvitation(id, RequestStatus.REJECTED);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters){
        List<GoalInvitation> goalInvitation = goalInvitationRepository.findAll();

        return invitationFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(goalInvitation.stream(),
                        (goalInvitationStream, filter) -> filter.apply(goalInvitationStream, filters),
                        (list1, list2) -> list1)
                .map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitationDto decisionOnGoalInvitation(Long id, RequestStatus status){
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объект не найден"));
        goalInvitation.setStatus(status);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }
}