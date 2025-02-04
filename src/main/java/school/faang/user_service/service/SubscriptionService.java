package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationDto;

import java.util.List;

public interface SubscriptionService {
    List<RecommendationDto> getAllGivenRecommendations(long authorId);
}
