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
            example = "Отличный специалист с глубокими знаниями в области машинного обучения",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 1000
    )
    private String content;

    @Schema(
            description = "Список предлагаемых навыков",
            implementation = SkillOfferCreateDto.class
    )
    private List<SkillOfferCreateDto> skillOffers;
}