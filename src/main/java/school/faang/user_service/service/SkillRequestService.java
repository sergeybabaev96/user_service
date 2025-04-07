package school.faang.user_service.service;

import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

public interface SkillRequestService {
    List<SkillRequest> getSkillRequestsByRequestId(long requestId);

    void createSkillRequest(long requestId, long skillId);
}
