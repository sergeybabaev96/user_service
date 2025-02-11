package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.filter.InvitationFilter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private static final int MAX_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper mapper;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final List<InvitationFilter> invitationFilters;

    @Override
    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        checkInvitationUsers(goalInvitationDto);
        goalInvitationRepository.save(mapper.toEntity(goalInvitationDto));
    }

    @Override
    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findGoalInvitationById(goalInvitationId);
        long invitedUserId = goalInvitation.getInvited().getId();
        long goalId = goalInvitation.getGoal().getId();
        User invitedUser = getUserById(invitedUserId);
        checkUserAndGoal(invitedUser, goalId);
        acceptInvitation(goalInvitationId);
        addNewGoal(goalId, invitedUser);
    }

    @Override
    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findGoalInvitationById(goalInvitationId);
        long goalId = goalInvitation.getGoal().getId();
        checkNotExistGoal(goalId);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    @Override
    public List<GoalInvitationDto> getInvitationsWithFilters(InvitationFilterDto filters) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();
        for (InvitationFilter filter : invitationFilters) {
            if (filter.isApplicable(filters)) {
                goalInvitations = filter.apply(goalInvitations, filters);
            }
        }
        return goalInvitations.stream()
                .map(mapper::toDTO)
                .toList();
    }

    private void checkInvitationUsers(GoalInvitationDto dto) {
        long inviterId = dto.getInviterId();
        checkInviter(inviterId);
        long invitedUserId = dto.getInvitedUserId();
        checkInvitedUser(invitedUserId);
        checkDifferentUsers(inviterId, invitedUserId);
    }

    private void checkInviter(long inviterId) {
        if (!userRepository.existsById(inviterId)) {
            throw new EntityNotFoundException(
                    String.format("Inviter user with id = %d doesn't exist", inviterId));
        }
    }

    private void checkInvitedUser(long invitedUserId) {
        if (!userRepository.existsById(invitedUserId)) {
            throw new EntityNotFoundException(
                    String.format("Invited user with id = %d doesn't exist", invitedUserId));
        }
    }

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(
                        String.format("User with id = % doesn't exist", id)));
    }

    private void checkDifferentUsers(long inviterId, long invitedUserId) {
        if (inviterId == invitedUserId) {
            throw new IllegalArgumentException("Inviter user and invited user not different");
        }
    }

    private void checkUserAndGoal(User user, long goalId) {
        checkGoalFewerThanMax(user);
        checkNotExistGoal(goalId);
        checkGoalNotInWork(user, goalId);
    }

    private void checkGoalFewerThanMax(User user) {
        if (isGoalsMoreThanMax(user)) {
            throw new EntityNotFoundException("User goals is more than max");
        }
    }

    private GoalInvitation findGoalInvitationById(Long goalId) {
        return goalInvitationRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Invitation with id = %d doesn't exist", goalId)));
    }

    private void checkNotExistGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException(
                    String.format("Goal with id = %d doesn't exists", goalId));
        }
    }

    private void checkGoalNotInWork(User user, long goalId) {
        if (isGoalInWorkByUser(user, getGoalById(goalId))) {
            throw new EntityNotFoundException(
                    String.format("Goal with id = %d already in work for user with id = %d", goalId, user.getId()));
        }
    }

    private boolean isGoalsMoreThanMax(User user) {
        return getActiveGoalsCount(user) >= MAX_GOALS;
    }

    private static int getActiveGoalsCount(User user) {
        return user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .toList()
                .size();
    }

    private boolean isGoalInWorkByUser(User user, Goal currentGoal) {
        return user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .anyMatch(goal -> goal.getId().equals(currentGoal.getId()));
    }

    private void acceptInvitation(long invitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(invitationId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Invitation with id = %d doesn't exist", invitationId)));
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(goalInvitation);
    }

    private void addNewGoal(long goalId, User user) {
        Goal newGoal = getGoalById(goalId);
        newGoal.getUsers().add(user);
        goalRepository.save(newGoal);
    }

    private Goal getGoalById(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Goal with id = %d doesn't exist", id)));
    }
}
