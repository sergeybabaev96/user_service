package school.faang.user_service.filter.recommendation.filters;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
public class CreatedAtFilter implements RecommendationRequestFilterStrategy {

    private final LocalDateTime createdAt;

    @Override
    public boolean filter(RecommendationRequest request) {
        return Objects.equals(request.getCreatedAt(), createdAt);
    }
}