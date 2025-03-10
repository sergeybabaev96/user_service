package school.faang.user_service.dto.recommendation;


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
 *     <li>Включает поля:{@code createdAt}, {@code updatedAt}, которые генерируются сервером при сохранении.</li>
 *     <li>Используется для представления (отображения) данных о уже существующей рекомендации.</li>
 * </ul>
 */
@Data
public class RecommendationViewDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<SkillOfferViewDto> skillOffers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
