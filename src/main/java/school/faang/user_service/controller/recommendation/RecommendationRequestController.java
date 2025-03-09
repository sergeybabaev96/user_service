package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);

        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filterDto) {
        return recommendationRequestService.getRequests(filterDto);
    }

    private static void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.message().isBlank()) {
            throw new DataValidationException("Recommendation request's message is required");
        }
    }
}
