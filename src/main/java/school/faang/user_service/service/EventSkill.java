package school.faang.user_service.service;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

public interface EventSkill {
    void checkSkillsToUser(EventDto eventDto);

    List<Skill> getSkills(List<Long> relatedSkills);
}
