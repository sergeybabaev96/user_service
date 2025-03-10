package school.faang.user_service.dto.skilloffer;

import lombok.Data;
/**
 * DTO для создания предложения навыка.
 * Используется для передачи информации о навыке, который предлагается в рамках рекомендации.
 * Разница с {@link SkillOfferViewDto}:
 * <ul>
 *     <li>Не включает поле {@code id}, так как идентификатор создается автоматически в базе данных.</li>
 *     <li>Используется только для создания нового предложения навыка в рамках рекомендации.</li>
 * </ul>
 */
@Data
public class SkillOfferCreateDto {
    private Long skillId;

}
