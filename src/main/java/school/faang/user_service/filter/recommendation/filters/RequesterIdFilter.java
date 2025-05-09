package school.faang.user_service.filter.recommendation.filters;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;

import java.util.Objects;

@RequiredArgsConstructor
public class RequesterIdFilter implements RecommendationRequestFilterStrategy {

    private final Long requesterId;

    @Override
    public boolean filter(RecommendationRequest request) {
        return Objects.equals(request.getRequester().getId(), requesterId);
    }
}
