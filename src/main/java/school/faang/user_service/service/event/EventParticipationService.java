package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationService {
    @Autowired
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId)) {
            throw new RuntimeException("User is already registered");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .noneMatch((user -> user.getId() == userId))) {
            throw new RuntimeException("User is not registered");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {  //???
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(user -> new UserDto(user.getId(),
                        user.getUsername(),
                        user.getEmail()))
                .toList();
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
