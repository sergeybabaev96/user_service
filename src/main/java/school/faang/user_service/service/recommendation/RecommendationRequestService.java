package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequest create(RecommendationRequestDto recommendationRequest);

    List<RecommendationRequestDto> getRequestByFilter(RecommendationRequestFilterDto dto);

    RecommendationRequest getRequestById(Long id);
}
