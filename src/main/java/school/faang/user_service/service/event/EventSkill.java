package school.faang.user_service.service.event;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;

@Service
@Data
public class EventSkill {
    private final SkillRepository skillRepository;

    public boolean checkSkillsToUser(EventDto eventDto) {
        List<Long> skillsOfUserId = skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                .map(Skill::getId).toList();
        if (new HashSet<>(skillsOfUserId).containsAll(eventDto.getRelatedSkills())) {
            return true;
        } else {
            return false;
        }
    }

    public List<Skill> getSkills(List<Long> relatedSkills) {
        return skillRepository.findAllById(relatedSkills);
    }

}
