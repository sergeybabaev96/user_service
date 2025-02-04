package school.faang.user_service.service.skill;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found skill by id: " + id));
    }
}
