package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest);

    List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter, int pageNumber, int pageSize);

    RecommendationRequestDto getRequestById(long id);

    void rejectRequest(long id, RejectionDto rejection);
}
