package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> findAllByRecommendationId(long recommendationId) {
        return skillOfferRepository.findAllByRecommendationId(recommendationId);
    }

    public void deleteAllByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public Optional<SkillOffer> findBySkillIdAndRecommendationId(long skillId, long recommendationId) {
        return skillOfferRepository.findBySkillIdAndRecommendationId(skillId, recommendationId);
    }

    public void create(long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }
}
