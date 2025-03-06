package school.faang.user_service.dto.recomendation;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationRequestDto(
        Long id,
        String message,
        RequestStatus status,
        String rejectionReason,
        List<Long> skillIds,
        Long requesterId,
        Long receiverId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
