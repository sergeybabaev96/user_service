package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    public List<Skill> getSkillsByIds(List<Long> skills) {
        return skillRepository.getSkillsByIds(skills);
    }
}
