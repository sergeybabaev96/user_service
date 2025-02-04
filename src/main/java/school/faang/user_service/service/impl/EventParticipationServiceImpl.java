package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void registerParticipant(long eventId, long userId) {
        boolean isParticipantFound = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (isParticipantFound) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant already exists");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    @Transactional
    public void unregister(long eventId, long userId) {
        boolean isParticipantNotFound = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .allMatch(user -> user.getId() != userId);
        if (isParticipantNotFound) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "participant not found");
        }
        eventParticipationRepository.unregister(eventId, userId);

    }

    @Override
    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
