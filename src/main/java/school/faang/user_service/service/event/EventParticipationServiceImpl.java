package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventExistException;
import school.faang.user_service.exception.EventParticipationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository participationRepository;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;

    public void registerParticipant(long eventId, long userId) {
        isEventExist(eventId);
        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new EventParticipationException(
                    "User with id = %d is already registered for the event with id = %d."
                            .formatted(userId, eventId));
        }
        participationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        isEventExist(eventId);
        if (isUserRegisteredForEvent(eventId, userId)) {
            participationRepository.unregister(eventId, userId);
        } else {
            throw new EventParticipationException(
                    "User with id = %d is not registered for the event with id = %d."
                            .formatted(userId, eventId));
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        isEventExist(eventId);
        List<User> participants = participationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.toUserDtoList(participants);
    }

    public long getParticipantsCount(long eventId) {
        isEventExist(eventId);
        return participationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return participationRepository.findAllParticipantsByEventId(eventId).stream()
                .mapToLong(User::getId).anyMatch(id -> id == userId);
    }

    private void isEventExist(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventExistException("Event with id = %d does not exist.".formatted(eventId));
        }
    }
}
