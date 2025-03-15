package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (participants.stream().anyMatch(user -> user.getId() == userId)) {
            throw new DataValidationException("User is already registered for this event.");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (participants.stream().noneMatch(user -> user.getId() == userId)) {
            throw new DataValidationException("User is not registered for this event.");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return UserMapper.usersToUserDtos(participants);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
