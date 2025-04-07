package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillRequestServiceImpl implements SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;

    @Override
    public List<SkillRequest> getSkillRequestsByRequestId(long requestId) {
        return skillRequestRepository.findAllByRequestId(requestId);
    }

    @Override
    public void createSkillRequest(long requestId, long skillId) {
        skillRequestRepository.create(requestId, skillId);
    }
}
