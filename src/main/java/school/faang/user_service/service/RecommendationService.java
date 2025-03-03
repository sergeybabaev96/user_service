package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
public class RecommendationService {
    RecommendationRepository recommendationRepository;
    SkillOfferRepository skillOfferRepository;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository,
                                 SkillOfferRepository skillOfferRepository) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferRepository = skillOfferRepository;
    }
}
