package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {
    @Min(value = 1, message = "Id must be greater than 0")
    private long id;
    @Size(max = 255, message = "The length must not exceed 255 characters")
    private String rejectionReason;
}
