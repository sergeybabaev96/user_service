package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("User with id " + eventDto.getOwnerId() + " not found"));

        Set<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredSkillIds = Set.copyOf(eventDto.getRelatedSkills());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            throw new DataValidationException("User does not have the required skills to create this event");
        }

        Event entity = eventMapper.toEntity(eventDto);
        entity.setOwner(owner);
        entity = eventRepository.save(entity);

        return eventMapper.toDto(entity);
    }
}
