package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
