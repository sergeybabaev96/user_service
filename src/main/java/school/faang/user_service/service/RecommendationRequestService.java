package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {
    @Transactional
    RecommendationRequestDto create(RecommendationRequestDto recommendationRequest);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto);

    RecommendationRequestDto getRequest(long id);

    boolean rejectRequest(long id, RejectionDto rejection);
}
