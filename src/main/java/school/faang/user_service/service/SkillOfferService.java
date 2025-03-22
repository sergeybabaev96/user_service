package school.faang.user_service.service;

import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.Optional;

public interface SkillOfferService {
    List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId);

    void deleteSkillOfferssByRecommendationId(long recommendationId);

    Optional<SkillOffer> findSkillOfferBySkillAndRecommendationIds(long skillId, long recommendationId);

    void createSkillOffer(long skillId, long recommendationId);
}
