package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationDto {
    private Long id;
    private Long goalId;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
