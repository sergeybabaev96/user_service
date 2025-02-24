package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateRecommendationRequestRequest(@NotEmpty String message,
                                                 List<Long> skills,
                                                 long requesterId,
                                                 long receiverId) {
}
