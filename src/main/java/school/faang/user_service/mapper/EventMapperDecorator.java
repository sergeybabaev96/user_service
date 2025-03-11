package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public abstract class EventMapperDecorator implements EventMapper {

    @Autowired
    @Qualifier("delegate")
    private EventMapper delegate;

    private final SkillRepository skillRepository;

    @Override
    public EventDto toDto(Event event) {
        return delegate.toDto(event);
    }

    @Override
    public Event toEntity(EventDto eventDto) {
        Event event = delegate.toEntity(eventDto);
        event.setRelatedSkills(mapIdsToSkills(eventDto.getRelatedSkills()));
        return event;
    }

    @Override
    public void updateEventFormDto(EventDto eventDto, Event event) {
        delegate.updateEventFormDto(eventDto, event);
        event.setRelatedSkills(mapIdsToSkills(eventDto.getRelatedSkills()));
    }

    private List<Skill> mapIdsToSkills(List<Long> skillIds) {
        return skillIds == null ? null : skillRepository.findAllById(skillIds);
    }
}
