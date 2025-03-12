package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        log.info("Starting update");
        RecommendationDto updatedRecommendationDto = recommendationService.update(recommendationDto);
        log.info("The update is finished");
        return updatedRecommendationDto;
    }

    public void deleteRecommendation(Long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        return recommendationService.getAllGivenRecommendation(authorId);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }
}
