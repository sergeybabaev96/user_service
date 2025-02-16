package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class MentorshipRequestDto {
    private long id;
    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "The length must not exceed 255 characters")
    private String description;
    @Min(value = 1, message = "Requester id must be greater than 0")
    private long requesterId;
    @Min(value = 1, message = "Mentor  id must be greater than 0")
    private long receiverId;
    private RequestStatus status;
}
