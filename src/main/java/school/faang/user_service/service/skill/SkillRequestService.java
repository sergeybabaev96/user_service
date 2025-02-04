package school.faang.user_service.service.skill;

import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface SkillRequestService {
    List<SkillRequestDto> createAllSkillRequest(List<Long> skillIds, RecommendationRequest recommendationRequest);
}
