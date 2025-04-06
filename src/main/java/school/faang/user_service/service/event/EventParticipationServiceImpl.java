package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Override
    public void registerParticipant(long eventId, long userId) {
        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new IllegalArgumentException("User is already registered for this event");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        if (!isUserRegisteredForEvent(eventId, userId)) {
            throw new IllegalArgumentException("User is not registered for this event");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Override
    public List<UserDto> getParticipants(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
