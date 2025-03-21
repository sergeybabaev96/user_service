package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @RequestMapping("/create")
    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @RequestMapping("/update")
    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.update(recommendation);
    }

    @RequestMapping("/delete")
    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    @RequestMapping("/getAllByReceiver")
    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    @RequestMapping("/getAllByAuthor")
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation == null || recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            log.info("Recommendation Is Not Valid");
            throw new DataValidationException("recommendation is not valid");
        }
    }
}
