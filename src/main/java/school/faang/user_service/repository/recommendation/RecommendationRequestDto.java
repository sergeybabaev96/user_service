package school.faang.user_service.repository.recommendation;

import org.springframework.lang.NonNull;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationRequestDto(
        long id,
        @NonNull String message,
        RequestStatus status,
        List<SkillRequestDto> skills,
        long requesterId,
        long receiverId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
