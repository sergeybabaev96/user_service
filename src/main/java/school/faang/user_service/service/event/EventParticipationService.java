package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.EventParticipationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
public class EventParticipationService {
    private final EventParticipationRepository participationRepository;
    private final UserMapper userMapper;

    @Autowired
    public EventParticipationService(EventParticipationRepository participationRepository, UserMapper userMapper) {
        this.participationRepository = participationRepository;
        this.userMapper = userMapper;
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

    public List<UserDto> getParticipant(long eventId) {
        List<User> participants = participationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.userToUserDtos(participants);
    }

    public long getParticipantsCount(long eventId) {
        return participationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return participationRepository.findAllParticipantsByEventId(eventId).stream()
                .mapToLong(User::getId).anyMatch(id -> id == userId);
    }
}
