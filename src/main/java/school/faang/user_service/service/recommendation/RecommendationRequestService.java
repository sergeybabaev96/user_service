package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recomendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto createRequest(RecommendationRequestRcvDto requestDto);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filters);

    RecommendationRequestDto getRequest(long id);

    RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto);
}
