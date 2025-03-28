package school.faang.user_service.service;

import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.Optional;

public interface SkillOfferService {

     List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId);

    void deleteSkillOffersByRecommendationId(long recommendationId);

    Optional<SkillOffer> findSkillOfferBySkillAndRecommendationIds(long skillId, long recommendationId);

    void createSkillOffer(long skillId, long recommendationId);


    void isEnoughAmountOffersToSkill(Long skillOfferId, Long userId);


    List<SkillOffer> getSkillOfferToUser(Long skillOfferId, Long userId);
}
