package school.faang.user_service.service.skill;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Data
@Service
public class SkillRequestService {

    private final SkillRequestRepository skillRequestRepository;

    public boolean skillRequestsExist(List<SkillRequest> skillRequests) {
        if (!skillRequests.isEmpty()) {
            return skillRequests.stream()
                    .allMatch(skillRequest -> skillRequestRepository.existsById(skillRequest.getId()));
        }

        return false;
    }

    public void createSkillRequestsFromList(List<SkillRequest> skillRequests) {
        skillRequests.forEach(skillRequest ->
                skillRequestRepository.create(skillRequest.getId(), skillRequest.getSkill().getId()));
    }
}
