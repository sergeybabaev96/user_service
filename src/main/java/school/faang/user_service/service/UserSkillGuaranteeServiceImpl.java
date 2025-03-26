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
public class UserSkillGuaranteeServiceImpl implements UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferServiceImpl skillOfferService;

    @Override
    public void createUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        userSkillGuaranteeRepository.create(userId, skillId, guarantorId);
    }

    @Override
    public Optional<UserSkillGuarantee> findUserSkillGuaranteeByGuarantorId(long guarantorId) {
        return userSkillGuaranteeRepository.findByGuarantorId(guarantorId);
    }

    @Override
    public void addUserSkillGuarantee(Long skillId, Long userId) {
        List<SkillOffer> skillOffers = skillOfferService.getSkillOfferToUser(skillId, userId);
        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .skill(skillOffer.getSkill())
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .user(skillOffer.getRecommendation().getReceiver()).build();
            userSkillGuaranteeRepository.save(guarantee);
        });
    }
}
