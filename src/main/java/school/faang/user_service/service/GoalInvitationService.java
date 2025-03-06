package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GoalInvitationService {
    private static final int MAX_GOAL_COUNT = 3;
    private final GoalInvitationRepository repository;
    private final UserSevice userSevice;
    private final GoalService goalService;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitation acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = checkExistingGoal(id);
        Goal goal = goalInvitation.getGoal();
        User invitedUser = goalInvitation.getInvited();

        List<Goal> goals = invitedUser.getGoals();
        if (goals.size() >= MAX_GOAL_COUNT) {
            throw new IllegalStateException("User has maximum goals");
        }
        if (goals.contains(goal)) {
            throw new IllegalStateException("User has this goal already");
        }

        goals.add(goal);
        userSevice.save(invitedUser);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        return repository.save(goalInvitation);
    }

    public GoalInvitation rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = checkExistingGoal(id);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        return repository.save(goalInvitation);
    }

    public List<GoalInvitation> getInvitations(InvitationFilterIDto filter) {
        List<GoalInvitation> goalInvitations = repository.findAll();
        return GoalInvitationFilter.filter(goalInvitations, filter);
    }

    private GoalInvitation checkExistingGoal(long id) {
        GoalInvitation goalInvitation = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GaolInvitation with id = " + id + " does not exist"));
        goalService.findById(goalInvitation.getGoal().getId())
                .orElseThrow(() -> new EntityNotFoundException("Goal with id = " + id + " does not exist"));

        return goalInvitation;
    }

    public Optional<GoalInvitation> findById(Long id) {
        return repository.findById(id);
    }

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        validateFields(goalInvitationDto, goalInvitation);

        return goalInvitationMapper.toDto(repository.save(goalInvitation));
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
