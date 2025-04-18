package school.faang.user_service.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;

import java.util.List;

/**
 * DTO для создания новой рекомендации.
 * Используется при создании рекомендации, где необходимо передать информацию
 * об авторе, получателе, содержимом и предложенных навыках.
 * Разница с {@link RecommendationViewDto}:
 * <ul>
 *     <li>Не включает поля: {@code createdAt}, {@code updatedAt}, {@code id} так как эти данные генерируются сервером.</li>
 *     <li>Используется только для создания новой рекомендации, а не для её отображения.</li>
 * </ul>
 */
@Data
public class RecommendationCreateDto {
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
            maxLength = 1000
    )
    private String content;

    @Schema(
            description = "List of recommendation skills",
            implementation = SkillOfferCreateDto.class
    )
    private List<SkillOfferCreateDto> skillOffers;
}