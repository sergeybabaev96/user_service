package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {

        if (eventParticipationRepository.checkUserEventRegistration(eventId, userId) > 0) {
            throw new IllegalStateException("User is already registered for this event.");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (eventParticipationRepository.checkUserEventRegistration(eventId, userId) == 0) {
            throw new IllegalStateException("User is not registered for this event.");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.usersToUserDtos(participants);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
