package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private final String notNullId = "Поле Id не может быть равным нулю";
    private final String positiveId = "Поле Id должно быть положительным числом";

    @NotNull(message = notNullId )
    @Positive(message = positiveId)
    private Long requesterId;
    @NotNull(message = notNullId )
    @Positive(message = positiveId)
    private Long receiverId;
    private RequestStatus status;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
