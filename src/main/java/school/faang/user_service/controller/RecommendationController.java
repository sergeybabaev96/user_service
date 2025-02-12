package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RecommendationDtoValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationDtoValidator recommendationDtoValidator;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationDtoValidator.validate(recommendationDto);
        Recommendation recommendation = recommendationService
                .create(recommendationDto.authorId(), recommendationDto.receiverId(),
                        recommendationDto.skillOffersId(), recommendationDto.content(),
                        recommendationDto.createdAt());
        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        recommendationDtoValidator.validate(updated);
        Recommendation recommendation = recommendationService
                .update(updated.authorId(), updated.receiverId(), updated.skillOffersId(),
                        updated.content(), updated.createdAt());
        return recommendationMapper.toDto(recommendation);
    }

    public void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        List<Recommendation> allUserRecommendations = recommendationService.getAllUserRecommendations(receiverId);
        return recommendationMapper.toListRecommendationDtos(allUserRecommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> allGivenRecommendations = recommendationService.getAllGivenRecommendations(authorId);
        return recommendationMapper.toListRecommendationDtos(allGivenRecommendations);
    }
}
