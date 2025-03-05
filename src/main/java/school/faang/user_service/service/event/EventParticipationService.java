package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.EventParticipationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
public class EventParticipationService {
    private EventParticipationRepository participationRepository;

    @Autowired
    public EventParticipationService(EventParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public void registerParticipant(long eventId, long userId) {
        if (!isUserRegisteredForEvent(eventId, userId)) {
            participationRepository.register(eventId, userId);
        } else {
            throw new EventParticipationException("This user is already registered for the event.");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (isUserRegisteredForEvent(eventId, userId)) {
            participationRepository.unregister(eventId, userId);
        } else {
            throw new EventParticipationException("This user is not registered for the event.");
        }
    }

    public List<User> getParticipant(long eventId) {
        return participationRepository.findAllParticipantsByEventId(eventId);
    }

    public long getParticipantsCount(long eventId) {
        return participationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return participationRepository.findAllParticipantsByEventId(eventId).stream()
                .mapToLong(User::getId).anyMatch(id -> id == userId);
    }
}
