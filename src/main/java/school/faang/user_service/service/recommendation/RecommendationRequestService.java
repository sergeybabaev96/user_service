package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

public interface RecommendationRequestService {
    RecommendationRequestDto create(RecommendationRequestDto requestDto);
}
