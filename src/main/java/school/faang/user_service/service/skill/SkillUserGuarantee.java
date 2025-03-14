package school.faang.user_service.service.skill;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.List;

@Service
@Data
public class SkillUserGuarantee {

    private final UserSkillGuaranteeRepository guaranteeRepository;
    private final SkillOfferService skillOfferService;

    public void addUserSkillGuarantee(Long skillId, Long userId) {
        List<SkillOffer> skillOffers = skillOfferService.getSkillOfferToUser(skillId, userId);
        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skillOffer.getSkill());
            guarantee.setGuarantor(skillOffer.getRecommendation().getAuthor());
            guarantee.setUser(skillOffer.getRecommendation().getReceiver());
        });
    }
}
