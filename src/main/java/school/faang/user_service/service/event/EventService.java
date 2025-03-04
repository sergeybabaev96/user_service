package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository1, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository1;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }
    // Валидатор: событие != null, название не пустое, пользователь != null, дата начала >= сегодня
    public void isValid(EventDTO event) {
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
    }

    public EventDTO create(EventDTO event) {
        //eventOwner пользователь из event, получаем из базы, чтобы сравнить скиллы для создания event-a
        User eventOwner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        //преобразуем навыки пользователя в их id, для дальнейшего сравнения
        List<Long> ownerSkillsIDs = eventOwner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        //Проверяем, есть ли в навыках пользователя навык, который связан с объявленными навыками события
        boolean matchSkills =  event.getRelatedSkills().stream()
                .anyMatch(ownerSkillsIDs::contains);

        if (!eventOwner.getSkills().isEmpty() && matchSkills) {
            Event entityEvent = eventMapper.eventDTOToEvent(event);
            //убеждаемся что владелец точно установился для формирования правильных связей.
            entityEvent.setOwner(eventOwner);
            eventRepository.save(entityEvent);
            return eventMapper.eventToEventDTO(entityEvent);
        } else {
            throw new DataValidationException("Skills are empty or not match");
        }
    }
}
