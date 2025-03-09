package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> findAllOffersOfSkill(long skillId, long userId) {
        return skillOfferRepository.findAllOffersOfSkill(skillId, userId);
    }
}
