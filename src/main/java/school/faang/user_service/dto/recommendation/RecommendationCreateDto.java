package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;

import java.util.List;

/**
 * DTO для создания новой рекомендации.
 * Используется при создании рекомендации, где необходимо передать информацию
 * об авторе, получателе, содержимом и предложенных навыках.
 * Разница с {@link RecommendationViewDto}:
 * <ul>
 *     <li>Не включает поля: {@code createdAt}, {@code updatedAt}, так как эти данные генерируются сервером.</li>
 *     <li>Используется только для создания новой рекомендации, а не для её отображения.</li>
 * </ul>
 */
@Data
public class RecommendationCreateDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<SkillOfferCreateDto> skillOffers;
}
