package school.faang.user_service.dto.recommendation;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @NotNull(message = "authorId не может быть null")
    private Long authorId;

    @NotNull(message = "receiverId не может быть null")
    private Long receiverId;

    @NotBlank(message = "content не может быть null")
    private String content;

    private List<SkillOfferViewDto> skillOffers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
