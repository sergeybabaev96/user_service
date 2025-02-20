package school.faang.user_service.dto.recommendation.request.filter;

import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestFilterDto);

    List<RecommendationRequest> apply(List<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto);
}
