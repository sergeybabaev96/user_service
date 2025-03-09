package school.faang.user_service.service.skill;

import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
public class SkillService {
    public SkillRepository skillRepository;

    public boolean isAllSkillsExist(List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                return false;
            }
        }
        return true;
    }
}