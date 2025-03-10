package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectionDto {
    @NotBlank (message = "Provide rejection reason")
    private String rejectionReason;
}
