package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;
import java.util.Optional;

public interface RecommendationRequestService {

    Optional<RecommendationRequestDto> create(RecommendationRequestDto recommendationRequestDto);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);

    RecommendationRequestDto getRequest(long id);

    RecommendationRequestDto rejectRequest(long id, RejectionDto rejection);
}
