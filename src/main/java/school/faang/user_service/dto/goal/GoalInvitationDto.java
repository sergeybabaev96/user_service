package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {

    private Long id;

    @NotNull(message = "Inviter id can't be null")
    @Min(value = 0, message = "Inviter id should be positive number")
    private Long inviterId;

    @NotNull(message = "Invited id can't be null")
    @Min(value = 0, message = "Invited id should be positive number")
    private Long invitedUserId;

    @NotNull(message = "Goal id can't be null")
    @Min(value = 0, message = "Goal id should be positive number")
    private Long goalId;

    private RequestStatus status;
}
