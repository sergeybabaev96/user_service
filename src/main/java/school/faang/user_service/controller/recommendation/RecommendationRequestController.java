package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);

        return recommendationRequestService.create(recommendationRequest);
    }

    private static void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.message().isBlank()) {
            throw new DataValidationException("Recommendation request's message is required");
        }
    }
}
