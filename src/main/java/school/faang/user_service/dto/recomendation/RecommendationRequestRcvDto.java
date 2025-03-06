package school.faang.user_service.dto.recomendation;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationRequestRcvDto(
        String message,
        List<Long> skillIds,
        Long requesterId,
        Long receiverId
) {
}
