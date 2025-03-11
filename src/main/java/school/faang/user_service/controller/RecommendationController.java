package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;
    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
        return recommendation;
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        validateRecommendation(updated);
        recommendationService.update(updated);
        return updated;
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("The recommendation is empty");
        }
    }
}