package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public void updateAll(List<Skill> skills) {
        skillRepository.saveAllAndFlush(skills);
    }

    @Override
    public Skill findById(Long id) {
        return skillRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Skill id " + id));
    }

    @Override
    public List<Skill> findAllById(List<Long> ids) {
        return skillRepository
                .findAllById(ids);
    }

    @Override
    public List<Skill> findAllByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }
}
