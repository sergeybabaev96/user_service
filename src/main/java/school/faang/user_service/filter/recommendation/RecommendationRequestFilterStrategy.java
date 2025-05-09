package school.faang.user_service.filter.recommendation;

import school.faang.user_service.entity.recommendation.RecommendationRequest;

public interface RecommendationRequestFilterStrategy {

    boolean filter(RecommendationRequest recommendationRequest);
}
