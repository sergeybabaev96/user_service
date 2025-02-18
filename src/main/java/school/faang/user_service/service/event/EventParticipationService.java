package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isRegistered = users.stream()
                .anyMatch(user -> Objects.equals(user.getId(), userId));
        if (isRegistered) {
            throw new IllegalArgumentException("User %d already registered to the event %d".formatted(userId, eventId));
        }
        eventParticipationRepository.register(eventId, userId);
        log.info("User: {} registered to the event: {}", userId, eventId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isRegistered = users.stream()
                .anyMatch(user -> Objects.equals(user.getId(), userId));
        if (isRegistered) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("Registration is cancelled to user: {} from the event: {}", userId, eventId);
        } else {
            throw new IllegalArgumentException("User %d is not registered to the event %d".formatted(userId, eventId));
        }
    }

    @Transactional
    public List<User> getParticipants(long eventId) {
        log.info("Registered users to the event: {}", eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        log.info("The number of all registered users to the event: {}", eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}