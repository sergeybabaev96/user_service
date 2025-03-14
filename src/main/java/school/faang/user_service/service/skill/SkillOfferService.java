package school.faang.user_service.service.skill;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@Data
public class SkillOfferService {

    private SkillOfferRepository skillOfferRepository;

    public boolean checkAmountOffersToSkill(Long skillOfferId, Long userId) {
        int amountOffers = skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId).size();
        return amountOffers < 3;
    }

    public List<SkillOffer> getSkillOfferToUser(Long skillOfferId, Long userId) {
        return skillOfferRepository.findAllOffersOfSkill (skillOfferId,userId);
    }
}
