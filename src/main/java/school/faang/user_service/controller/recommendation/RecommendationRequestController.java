package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dtovalidator.recommendation.RecommendationRequestParamValidator;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService requestService;
    private final RecommendationRequestParamValidator validator;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        validator.validate(recommendationRequest);
        return requestService.create(recommendationRequest);
    }
}
