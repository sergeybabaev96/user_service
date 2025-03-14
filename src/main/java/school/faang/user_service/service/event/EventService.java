package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventSkill eventSkill;
    private final EventOwner eventOwner;

    public EventDto create(EventDto eventDto) {
        if (!eventSkill.checkSkillsToUser(eventDto)) {
            throw new DataValidationException("The creator does not have enough skills");
        }
        Event event = eventMapper.eventDtoToEvent(eventDto);
        event.setOwner(eventOwner.getOwner(eventDto.getOwnerId()));
        event.setRelatedSkills(eventSkill.getSkills(eventDto.getRelatedSkills()));
        Event savedEvent = eventRepository.save(event);

        return eventMapper.eventToEventDto(savedEvent);
    }
}
