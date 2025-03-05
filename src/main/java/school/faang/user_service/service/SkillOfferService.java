package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public SkillOffer findById(long skillId) {
        return skillOfferRepository.findById(skillId)
                .orElseThrow(() -> new DataRetrievalFailureException("Не удалось получить предложение скилла"));
    }

    public void deleteAllByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public long create(long skillId, long recommendationId) {
        return skillOfferRepository.create(skillId, recommendationId);
    }
}
