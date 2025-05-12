package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendation) throws DataValidationException {
        if (recommendationValidation(recommendation)) {
            recommendationService.create(recommendation);
        }
        throw new DataValidationException("Empty content");
    }

    private boolean recommendationValidation(RecommendationDto recommendation) {
        return !recommendation.getContent().isEmpty();
    }
}
