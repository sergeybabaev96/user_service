package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    @Value("${user_service.skill.minSkillOffers}")
    private int minSkillOffers;
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId) {
        return skillOfferRepository.findAllByRecommendationId(recommendationId);
    }

    public void deleteSkillOfferssByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public Optional<SkillOffer> findSkillOfferBySkillAndRecommendationIds(long skillId, long recommendationId) {
        return skillOfferRepository.findBySkillIdAndRecommendationId(skillId, recommendationId);
    }

    public void createSkillOffer(long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }

    public void isEnoughAmountOffersToSkill(Long skillOfferId, Long userId) {
        int amountOffers = skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId).size();
        if (amountOffers < minSkillOffers) {
            throw new DataValidationException("User has less than 3 offers");
        };
    }

    public List<SkillOffer> getSkillOfferToUser(Long skillOfferId, Long userId) {
        return skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId);
    }
}
