package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserParticipantDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.user.UserParticipantMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipantServiceImpl implements EventParticipantService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserParticipantMapper userParticipantMapper;

    @Transactional
    @Override
    public void registerParticipant(long eventId, long userId) {
        checkEvent(eventId);
        checkUser(userId);
        eventParticipationRepository.register(eventId, userId);
        log.info("User with id = {} registered in event with id = {}", userId, eventId);
    }

    @Transactional
    @Override
    public void unregisterParticipant(long eventId, long userId) {
        checkEvent(eventId);
        checkUser(userId);
        eventParticipationRepository.unregister(eventId, userId);
        log.info("User with id = {} unregistered in event with id = {}", userId, eventId);
    }

    @Override
    public List<UserParticipantDto> findAllParticipantByEventId(long eventId) {
        checkEvent(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userParticipantMapper::toDto)
                .toList();
    }

    @Override
    public int countParticipant(long eventId) {
        checkEvent(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void checkEvent(long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.error("Event with id = {} not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id = %d not found", eventId));
        }
        log.info("Event with id = {} is exists", eventId);
    }

    private void checkUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id = {} not found", userId);
            throw new EntityNotFoundException(String.format("User with id = %d not found", userId));
        }
        log.info("User with id = {} is exists", userId);
    }
}
