package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationRequestDto(long id, String message, List<Long> skillsId, Long requesterId,
                                       Long receiverId, LocalDateTime createdAt, LocalDateTime updatedAt) {

}
