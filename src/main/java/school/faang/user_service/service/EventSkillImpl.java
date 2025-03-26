package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSkillImpl implements EventSkill {
    private final SkillRepository skillRepository;

    @Override
    public void checkSkillsToUser(EventDto eventDto) {
        List<Long> skillsOfUserId = skillRepository.findAllByUserId(eventDto.ownerId()).stream()
                .map(Skill::getId).toList();
        if (!new HashSet<>(skillsOfUserId).containsAll(eventDto.relatedSkills())) {
            throw new DataValidationException("The creator does not have enough skills");
        }
    }

    @Override
    public List<Skill> getSkills(List<Long> relatedSkills) {
        return skillRepository.findAllById(relatedSkills);
    }

}
