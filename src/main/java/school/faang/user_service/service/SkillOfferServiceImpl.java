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
public class SkillOfferServiceImpl implements SkillOfferService {
    @Value("${user_service.skill.minSkillOffers}")
    private int minSkillOffers;
    
    private final SkillOfferRepository skillOfferRepository;

    @Override
    public List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId) {
        return skillOfferRepository.findAllByRecommendationId(recommendationId);
    }

    @Override
    public void deleteSkillOffersByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    @Override
    public Optional<SkillOffer> findSkillOfferBySkillAndRecommendationIds(long skillId, long recommendationId) {
        return skillOfferRepository.findBySkillIdAndRecommendationId(skillId, recommendationId);
    }

    @Override
    public void createSkillOffer(long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }

    @Override
    public void isEnoughAmountOffersToSkill(Long skillOfferId, Long userId) {
        int amountOffers = skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId).size();
        if (amountOffers < minSkillOffers) {
            throw new DataValidationException("User has less than 3 offers");
        };
    }

    @Override
    public List<SkillOffer> getSkillOfferToUser(Long skillOfferId, Long userId) {
        return skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId);
    }
}
