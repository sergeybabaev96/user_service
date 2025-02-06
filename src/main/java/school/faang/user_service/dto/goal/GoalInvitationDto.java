package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Schema(description = "DTO Прглашения к участию в цели")
public class GoalInvitationDto {
    @Schema(description = "Уникальный идентификатор приглашения", example = "1")
    @Min(value = 1, message = "ID приглашения указано неверно")
    private long id;

    @Schema(description = "ID пользователя, который отправил приглашение", example = "10")
    @Min(value = 1, message = "ID приглашающего пользователя указано неверно")
    private long inviterId;

    @Schema(description = "ID пользователя, которому отправлено приглашение", example = "20")
    @Min(value = 1, message = "ID приглашенного пользователя указано неверно")
    private long invitedUserId;

    @Schema(description = "ID цели, к которой относится приглашение", example = "100")
    @Min(value = 1, message = "ID цели приглашения указано неверно")
    private long goalId;

    @Schema(description = "Статус приглашения (PENDING, ACCEPTED, REJECTED)", example = "PENDING")
    @NotNull(message = "Статус указан неверно")
    private RequestStatus status;
}
