package school.faang.user_service.repository.recommendation;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDto(
        long id,
        long authorId,
        long receiverId,
        String content,
        List<SkillOfferDto> skillOffers,
        LocalDateTime createdAt) {
}
