package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto create(RecommendationRequestDto requestDto);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);
}
