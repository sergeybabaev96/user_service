package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {

    boolean isApplicable(RequestFilterDto dto);

    Stream<RecommendationRequest> apply (Stream<RecommendationRequest> recommendationRequestStream
            , RequestFilterDto dto);

}
