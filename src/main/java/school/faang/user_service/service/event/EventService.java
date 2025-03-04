package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;

@Service
public class EventService {

    private final EventRepository eventRepository;


    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean isValid(EventDTO event) {
        LocalDate today = LocalDate.now();
        if (event == null) {
            throw new DataValidationException("Event is null");
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("Title is blank");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Owner id is null");
        }
        if (event.getStartDate().toLocalDate().isBefore(today)){
            throw new DataValidationException("Start date is before today");
        }
        return true;
    }
}
