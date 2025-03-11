package school.faang.user_service.dto.skilloffer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * DTO для представления предложенного навыка в рамках рекомендации.
 * Используется для отображения предложенных навыков, их идентификаторов.
 * Разница с {@link SkillOfferCreateDto}:
 * <ul>
 *     <li>Включает поле {@code id}, которое является уникальным идентификатором предложения навыка в базе данных.</li>
 *     <li>Используется для отображения данных о существующих предложениях навыков в рекомендациях.</li>
 * </ul>
 */
@Data
public class SkillOfferViewDto {
    @NotNull(message = "SkillOfferId не может быть null")
    private Long id;

    @NotNull(message = "skillId не может быть null")
    private Long skillId;
}
