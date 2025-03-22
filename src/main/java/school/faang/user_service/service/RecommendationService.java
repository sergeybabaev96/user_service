package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

public interface RecommendationService {
    @Transactional
    RecommendationDto create(@NotNull RecommendationDto recommendationDto);

    @Transactional
    RecommendationDto update(@NotNull RecommendationDto recommendationDto);

    void deleteRecommendationById(long recommendationId);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);

    List<RecommendationDto> getAllGivenRecommendations(long authorId);

    Recommendation findRecommendationById(long recommendationId);
}
