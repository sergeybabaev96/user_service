package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.update(recommendation);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    public void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("recommendation is not valid");
        }
    }
}
