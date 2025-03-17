package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {

    private static final int MIN_SKILL_OFFERS = 3;

    private SkillOfferRepository skillOfferRepository;

    public void isEnoughAmountOffersToSkill(Long skillOfferId, Long userId) {
        int amountOffers = skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId).size();
        if (amountOffers < MIN_SKILL_OFFERS) {
            throw new DataValidationException("User has less than 3 offers");
        };
    }

    public List<SkillOffer> getSkillOfferToUser(Long skillOfferId, Long userId) {
        return skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId);
    }
}
