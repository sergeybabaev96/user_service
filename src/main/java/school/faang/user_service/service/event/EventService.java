package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    public void deleteEvent(Event event) {
        List<User> usersOfEvent = event.getAttendees();
        event.getAttendees().removeAll(usersOfEvent);
        eventRepository.save(event);
        eventRepository.deleteById(event.getId());
    }
}
