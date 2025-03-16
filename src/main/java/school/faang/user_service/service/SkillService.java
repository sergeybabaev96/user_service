package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean doesSkillExists(long skillId) {
        return skillRepository.existsById(skillId);
    }

    public List<Skill> findSkillsByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }
}
