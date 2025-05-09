package school.faang.user_service.service.skill;

import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

public interface SkillRequestService {

    boolean skillRequestsExist(List<SkillRequest> skillRequests);

    void createSkillRequestsFromList(List<SkillRequest> skillRequests);
}
