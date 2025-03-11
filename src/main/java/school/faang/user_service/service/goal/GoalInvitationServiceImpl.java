package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoResponse;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvitationEntityNotFoundException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.service.goal.validator.InvitationDtoValidator;

import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final InvitationDtoValidator invitationDtoValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final List<InvitationFilter> filters;

    @Override
    public GoalInvitationDtoResponse createInvitation(GoalInvitationDto goalInvitationDto) {

        invitationDtoValidator.validate(goalInvitationDto);
        log.info(String.format("Create invitation goalId: %s", goalInvitationDto.goalId()));

        GoalInvitation savedInvitation =
                goalInvitationRepository.save(goalInvitationMapper.toGoalInvitationEntity(goalInvitationDto));
        return goalInvitationMapper.toGoalInvitationDtoResponse(savedInvitation);
    }

    @Override
    public GoalInvitationDtoResponse acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with id: {}.", id);
        GoalInvitation goalInvitation = findGoalInvitationById(id);

        User invitedUser = goalInvitation.getInvited();
        boolean isUserAlreadyWorkingOnGoal = containsGoalWithId(invitedUser.getGoals(), id);

        if (isUserAlreadyWorkingOnGoal) {
            throw new IllegalArgumentException(String.format("Exception invited user, "
                    + "the invited user is already working on this goal with id= %s", id));
        }

        if (invitedUser.getReceivedGoalInvitations().size() > MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException(String.format("Exception invited user can`t "
                    + "have more than %s goal invitations!", MAX_ACTIVE_GOALS));
        }

        invitedUser.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invitedUser);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toGoalInvitationDtoResponse(goalInvitation);
    }

    @Override
    public GoalInvitationDtoResponse rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}.", id);

        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);

        return goalInvitationMapper.toGoalInvitationDtoResponse(invitation);
    }

    @Override
    public List<GoalInvitationDtoResponse> getInvitations(InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        List<InvitationFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .toList();

        Stream<GoalInvitation> filteredStream = applicableFilters.isEmpty()
                ? Stream.empty()
                : applicableFilters.stream()
                .reduce(
                        invitations.stream(),
                        (currentStream, filter) -> filter.apply(currentStream, filterDto),
                        Stream::concat
                );

        return filteredStream.map(goalInvitationMapper::toGoalInvitationDtoResponse).toList();
    }


    private boolean containsGoalWithId(List<Goal> goals, long goalId) {
        return goals.stream().anyMatch(goal -> goal.getId() == goalId);
    }

    private GoalInvitation findGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException(
                        String.format("Invitation to a goal with id: %s, not found!", id)));
    }
}
