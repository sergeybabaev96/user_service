package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class MentorshipRequestDto {
    private long id;
    @NotBlank (message = "Description could not be blank")
    private String description;
    private long requesterId;
    private long receiverId;
    private String rejectionReason;
    private RequestStatus status;
}
