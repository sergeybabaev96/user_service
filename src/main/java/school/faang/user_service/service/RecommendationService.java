package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public Recommendation findById(long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataRetrievalFailureException("Recommendation is not found"));
    }
}
