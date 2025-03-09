package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
@Data
public class GoalInvitationFilterDto {
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
    @Size(max = 255, message = "Длина строки не должна превышать 255 символов")
    private String inviterNamePattern;
    @Size(max = 255, message = "Длина строки не должна превышать 255 символов")
    private String invitedNamePattern;
}
