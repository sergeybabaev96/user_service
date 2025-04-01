package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationDto {
    private Long id;

    @NotNull(message = "ID автора не может быть пустым")
    private Long authorId;

    @NotNull(message = "ID получателя не может быть пустым")
    private Long receiverId;

    @NotNull(message = "Содержание рекомендации не может быть пустым")
    @Size(min = 10, max = 1000, message = "Рекомендация должна содержать от 10 до 1000 символов")
    private String content;

    private List<SkillOfferDto> skillOffers;

    private LocalDateTime createdAt;
}
