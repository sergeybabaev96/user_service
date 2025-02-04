package school.faang.user_service.dto.recommendation.request.filter;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationRequestFilterDto {
    private final String message;
    private final String status;
    private final List<Long> skillIds;
    private final Long requesterId;
    private final Long receiverId;
    private final String createdAt;
    private final String updatedAt;
}
