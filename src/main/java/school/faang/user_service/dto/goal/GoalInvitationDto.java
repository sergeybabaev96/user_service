package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationDto {
    private final String notNullId = "Поле Id не может быть равным нулю";
    private final String positiveId = "Поле Id должно быть положительным числом";

    private Long id;
    @NotNull(message = notNullId)
    @Positive(message = positiveId)
    private Long inviterId;
    @NotNull(message = notNullId)
    @Positive(message = positiveId)
    private Long invitedUserId;
    @NotNull(message = notNullId)
    @Positive(message = positiveId)
    private Long goalId;
    private RequestStatus status;
}
