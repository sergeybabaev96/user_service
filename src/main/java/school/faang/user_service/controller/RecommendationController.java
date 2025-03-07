package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;
    private final SubscriptionService subscriptionService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        if (validateRecommendation(recommendationDto)) {
            recommendationService.create(recommendationDto);
        } else {
            throw new DataValidationException("The recommendation is empty");
        }
        return recommendationDto;
    }

    private boolean validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent().isBlank()) {
            return false;
        }
        return true;
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        if (validateRecommendation(updated)) {
            recommendationService.update(updated);
        } else {
            throw new DataValidationException("The recommendation is empty");
        }
        return updated;
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<Recommendation> getAllUserRecommendations(RecommendationDto recommendationDto) {
        List<RecommendationDto> recommendationDtos = recommendationService.getAllUserRecommendations(recommendationDto.getReceiverId());
        return recommendationDtos.stream().map(recommendationMapper::toEntity).toList();
    }

    public List<Recommendation> getAllGivenRecommendations(RecommendationDto recommendationDto) {
        List<RecommendationDto> recommendationDtos = subscriptionService.getAllGivenRecommendations(recommendationDto.getAuthorId());
        return recommendationDtos.stream().map(recommendationMapper::toEntity).toList();
    }
}
