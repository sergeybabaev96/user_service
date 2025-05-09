package school.faang.user_service.filter.recommendation.filters;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;

@RequiredArgsConstructor
public class SkillIdFilter implements RecommendationRequestFilterStrategy {
    
    private final Long skillId;

    @Override
    public boolean filter(RecommendationRequest request) {
        return request.getSkills().stream()
                .anyMatch(skill -> skill.getSkill().getId() == skillId);
    }
}
