package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public void updateAll(List<Skill> skills) {
        skillRepository.saveAllAndFlush(skills);
    }

    public Skill findById(Long id) {
        return skillRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("There is no Skill with id " + id));
    }

    public List<Skill> findAllByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }
}
