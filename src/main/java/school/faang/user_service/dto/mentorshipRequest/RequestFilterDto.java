package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    @Size(max = 255, message = "Длинна строки не должна превышать 255 символов")
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
