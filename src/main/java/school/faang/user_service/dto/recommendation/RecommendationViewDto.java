package school.faang.user_service.dto.recommendation;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.dto.skilloffer.SkillOfferViewDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для представления рекомендации.
 * Используется для отображения существующей рекомендации с полями,
 * такими как идентификатор, автор, получатель, содержание и время создания.
 * Разница с {@link RecommendationCreateDto}:
 * <ul>
 *     <li>Включает поля:{@code createdAt}, {@code updatedAt}, {@code id}которые генерируются сервером при сохранении.</li>
 *     <li>Используется для представления (отображения) данных о уже существующей рекомендации.</li>
 * </ul>
 */
@Data
public class RecommendationViewDto {
    @NotNull(message = "id не может быть null")
    @Schema(
            description = "Уникальный идентификатор рекомендации",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @NotNull(message = "authorId не может быть null")
    @Schema(
            description = "ID автора рекомендации",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long authorId;

    @NotNull(message = "receiverId не может быть null")
    @Schema(
            description = "ID получателя рекомендации",
            example = "456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long receiverId;

    @NotBlank(message = "content не может быть null")
    @Schema(
            description = "Текст рекомендации",
            example = "Исключительный специалист с глубокими знаниями Java и Spring",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 2000
    )
    private String content;

    @Schema(
            description = "Список предлагаемых навыков",
            implementation = SkillOfferViewDto.class
    )
    private List<SkillOfferViewDto> skillOffers;

    @Schema(
            description = "Дата и время создания рекомендации",
            example = "2023-05-15T14:30:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Дата и время последнего обновления рекомендации",
            example = "2023-05-16T10:15:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime updatedAt;
}