package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    @Size(max = 255, message = "The length must not exceed 255 characters")
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
