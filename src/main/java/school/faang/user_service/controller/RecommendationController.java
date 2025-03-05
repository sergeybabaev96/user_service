package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        return recommendationService.update(recommendation);
    }

    private static void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("Рекомендация должна содержать текст");
        }
    }
}
