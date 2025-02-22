package school.faang.user_service.dto.recommendation;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDto(
        @NonNull
        Long id,
        @NonNull
        Long authorId,
        @NonNull
        Long receiverId,
        @NonNull
        String content,
        @NonNull
        List<SkillOfferDto> skillOffersId,
        @NonNull
        LocalDateTime createdAt
) {
}
