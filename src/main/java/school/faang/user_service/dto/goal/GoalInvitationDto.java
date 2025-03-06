package school.faang.user_service.dto.goal;

public record GoalInvitationDto(
        Long inviterId,
        Long invitedUserId,
        Long goalId) {
}
