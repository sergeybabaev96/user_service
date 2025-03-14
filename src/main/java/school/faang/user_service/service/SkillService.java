package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean doesSkillExists(long skillId) {
        return skillRepository.existsById(skillId);
    }
}
