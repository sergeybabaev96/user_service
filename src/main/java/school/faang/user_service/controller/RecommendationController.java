package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        if (validateRecommendation(recommendationDto)) {
            recommendationService.create(recommendationDto);
        } else {
            throw new DataValidationException("The recommendation is empty");
        }
        return recommendationDto;
    }

    private boolean validateRecommendation(RecommendationDto recommendationDto) {
        return !recommendationDto.getContent().isBlank();
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

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        List<RecommendationDto> recommendationDtos = recommendationService.getAllUserRecommendations(receiverId);
        List<Recommendation> entities = recommendationDtos.stream().map(recommendationMapper::toEntity).toList();
        return entities.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<RecommendationDto> recommendationDtos = recommendationService.getAllGivenRecommendations(authorId);
        List<Recommendation> entities = recommendationDtos.stream().map(recommendationMapper::toEntity).toList();
        return entities.stream().map(recommendationMapper::toDto).toList();
    }
}
