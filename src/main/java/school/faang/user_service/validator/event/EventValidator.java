package school.faang.user_service.validator.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventValidator {

    private final EventRepository eventRepository;

    public void checkEventExistsById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("Event with eventId {} not found", eventId);
            throw new EntityNotFoundException("Event not found");
        }
    }
}
