package school.faang.user_service.filter.request;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

public interface RecommendationRequestFilter extends Filter<RecommendationRequest, RequestFilterDto> {
}
