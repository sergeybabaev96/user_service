package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDto {
    @NotBlank(message = "Причина отклонения запроса на менторство не может быть пустым.")
    private String rejectionReason;
}
