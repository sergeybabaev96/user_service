package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferService skillOfferService;

    public void createUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        userSkillGuaranteeRepository.create(userId, skillId, guarantorId);
    }

    public Optional<UserSkillGuarantee> findUserSkillGuaranteeByGuarantorId(long guarantorId) {
        return userSkillGuaranteeRepository.findByGuarantorId(guarantorId);
    }

    public void addUserSkillGuarantee(Long skillId, Long userId) {
        List<SkillOffer> skillOffers = skillOfferService.getSkillOfferToUser(skillId, userId);
        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skillOffer.getSkill());
            guarantee.setGuarantor(skillOffer.getRecommendation().getAuthor());
            guarantee.setUser(skillOffer.getRecommendation().getReceiver());
            userSkillGuaranteeRepository.save(guarantee);
        });
    }
}
