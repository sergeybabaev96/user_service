package school.faang.user_service.utils.goal;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.enums.goal.GoalStatus;
import school.faang.user_service.filter.goal.invitation.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class GoalInvitationPrepareData {
    private static final long INVITER_ID = 1L;
    private static final long INVITED_USER_ID = 2L;
    private static final long EXISTING_GOAL_ID = 1L;
    private static final long NEW_GOAL_ID = 3L;
    private static final long NEW_GOAL_INVITATION_ID = 1L;

    public static GoalInvitationDto getGoalInvitationDto(Long id, Long inviterId, Long invitedUserId, Long goalId) {
        return GoalInvitationDto.builder()
                .id(id)
                .inviterId(inviterId)
                .invitedUserId(invitedUserId)
                .goalId(goalId)
                .status(RequestStatus.PENDING)
                .build();
    }

    public static GoalInvitationDto getGoalInvitationDto() {
        return getGoalInvitationDto(NEW_GOAL_INVITATION_ID, INVITER_ID, INVITED_USER_ID, NEW_GOAL_ID);
    }

    public static User getUser(long id) {
        return User.builder()
                .id(id)
                .goals(new ArrayList<>())
                .receivedGoalInvitations(new ArrayList<>())
                .build();
    }

    public static Goal getActiveGoal(long id) {
        return Goal.builder()
                .id(id)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>())
                .build();
    }

    public static GoalInvitationFilterDto getInviterIdFilter() {
        return GoalInvitationFilterDto.builder()
                .inviterId(INVITER_ID)
                .build();
    }

    public static User getUserWithMaxGoals() {
        return User.builder()
                .id(INVITED_USER_ID)
                .goals(Arrays.asList(
                        Goal.builder().status(GoalStatus.ACTIVE).build(),
                        Goal.builder().status(GoalStatus.ACTIVE).build(),
                        Goal.builder().status(GoalStatus.ACTIVE).build()))
                .build();
    }

    public static User getUserWithAlreadyExistingGoal() {
        return User.builder()
                .id(INVITED_USER_ID)
                .goals(Arrays.asList(
                        Goal.builder().id(EXISTING_GOAL_ID).status(GoalStatus.ACTIVE).build(),
                        Goal.builder().id(NEW_GOAL_ID).status(GoalStatus.ACTIVE).build()))
                .receivedGoalInvitations(List.of())
                .build();
    }

    public static GoalInvitation getInvitationWithExistingGoal() {
        return GoalInvitation.builder()
                .id(NEW_GOAL_INVITATION_ID)
                .inviter(User.builder()
                        .id(INVITER_ID)
                        .build())
                .goal(
                        Goal.builder().id(EXISTING_GOAL_ID).build()
                )
                .build();
    }

    public static GoalInvitation getInvitationWithNewGoal(RequestStatus status) {
        return GoalInvitation.builder()
                .id(NEW_GOAL_INVITATION_ID)
                .inviter(User.builder()
                        .id(INVITER_ID)
                        .build())
                .goal(
                        Goal.builder()
                                .id(NEW_GOAL_ID)
                                .build()
                )
                .status(status)
                .build();
    }
}
