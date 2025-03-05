package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SkillRequestService {
    private final SkillRequestRepository repository;

    public SkillRequest findById(Long id) {
        return repository.findById(id).orElseThrow(()
                -> new NotFoundException("Skill request with id " + id + " not found"));
    }

    public List<SkillRequest> findByIds(List<Long> skillsIds) {
        return  repository.findBySkillIdIn(skillsIds);
    }

}
