package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    private final Long id;
    private final Long authorId;
    private final Long receiverId;
    private final String content;
    private final List<SkillOfferDto> skillOffers;
    private final LocalDateTime createdAt;
}
