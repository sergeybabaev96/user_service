package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private final String notNullId = "Поле Id не может быть равным нулю";
    private final String positiveId = "Поле Id должно быть положительным числом";

    private Long id;
    @NotBlank(message = "Описание запроса на менторство не может быть пустым.")
    private String description;
    @Positive(message = positiveId)
    @NotNull(message = notNullId)
    private Long requesterId;
    @Positive(message = positiveId)
    @NotNull(message = notNullId)
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;
}
