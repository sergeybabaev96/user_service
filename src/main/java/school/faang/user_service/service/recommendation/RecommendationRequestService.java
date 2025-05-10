package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto create(RecommendationRequestDto requestDto);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto);

    RecommendationRequestDto getRequest(Long id);

    RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection);
}
