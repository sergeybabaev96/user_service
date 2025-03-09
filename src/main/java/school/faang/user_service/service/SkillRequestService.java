package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@Service
@RequiredArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;

    public SkillRequest findById(long userId) {
        return skillRequestRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException("Skill request is not found"));
    }

    public SkillRequest create(long requestId, long skillId) {
        return skillRequestRepository.create(requestId, skillId);
    }
}
