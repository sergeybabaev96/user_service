package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class TestRecommendationRequestAcceptedFilterStutus implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto dto) {
        return true;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest>
                                                       recommendationRequestStream, RequestFilterDto dto) {
        return recommendationRequestStream.filter(recommendationRequest ->
                recommendationRequest.getStatus().equals(RequestStatus.ACCEPTED));
    }

}
