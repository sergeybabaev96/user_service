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
            description = "recommendation ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @NotNull(message = "authorId не может быть null")
    @Schema(
            description = "Recommendation author ID",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long authorId;

    @NotNull(message = "receiverId не может быть null")
    @Schema(
            description = "Recommendation receiver ID",
            example = "456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long receiverId;

    @NotBlank(message = "content не может быть null")
    @Schema(
            description = "Recommendation text",
            example = "Middle Java developer",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 2000
    )
    private String content;

    @Schema(
            description = "List of recommendation skills",
            implementation = SkillOfferViewDto.class
    )
    private List<SkillOfferViewDto> skillOffers;

    @Schema(
            description = "Date and Time of created recommendation",
            example = "2023-05-15T14:30:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Date and Time of updated recommendation",
            example = "2023-05-16T10:15:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime updatedAt;
}