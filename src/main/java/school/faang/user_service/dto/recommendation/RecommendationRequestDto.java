package school.faang.user_service.dto.recommendation;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationRequestDto(
        @Nullable Long id,
        @NonNull String message,
        RequestStatus status,
        List<SkillRequestDto> skills,
        long requesterId,
        long receiverId,
        @Nullable LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt) {
}
