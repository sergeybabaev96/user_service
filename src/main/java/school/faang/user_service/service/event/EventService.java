package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private void validateUserSkills(EventDto eventDto) {
        List<Long> requiredSkills = eventDto.getRelatedSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            User user = userRepository.findById(eventDto.getOwnerId())
                    .orElseThrow(() -> new DataValidationException("User not found."));
            if (!user.getSkills().containsAll(requiredSkills)) {
                throw new DataValidationException("User does not possess required skills.");
            }
        }
    }
}
