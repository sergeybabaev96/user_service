package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;


    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.message() == null || recommendationRequest.message().trim().isBlank()) {
            throw new IllegalArgumentException("Recommendation message cannot be null or empty.");
        }
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getFilteredRecommendationRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRecommendationRequestById(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);

    }
}
