package school.faang.user_service.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

/**
 *  Класс отвечает  за обработку запросов пользователя и валидацию этих запросов
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(@NonNull RecommendationDto recommendation) {
        validationRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(@NonNull RecommendationDto updated) {
        validationRecommendation(updated);
        return recommendationService.update(updated);
    }

    private void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }


    private void validationRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null) {
            log.error("Рекомендация = null");
            throw new DataValidationException("Recommendation context cannot be null");
        }
        if (recommendation.getContent().isEmpty()) {
            log.error("Рекомендация пустая");
            throw new DataValidationException("Recommendation context cannot be empty");
        }
    }


}
