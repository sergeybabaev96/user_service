package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillOfferServiceImpl implements SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    @Override
    public List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId) {
        return skillOfferRepository.findAllByRecommendationId(recommendationId);
    }

    @Override
    public void deleteSkillOfferssByRecommendationId(long recommendationId) {
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
}
