package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.ParticipantValidator;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ParticipantValidator participantValidator;

    public UserReadDto registerParticipant(long eventId, long userId) {

        participantValidator.checkParticipantAlreadyRegistered(eventId, userId);
        eventParticipationRepository.register(eventId, userId);

        User user = userRepository.getReferenceById(userId);
        return userMapper.toDto(userRepository.save(user));

    }

    public UserReadDto unregisterParticipant(long eventId, long userId) {

        participantValidator.checkParticipantNotRegistered(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);

        User user = userRepository.getReferenceById(userId);
        return userMapper.toDto(userRepository.save(user));

    }

    public List<UserReadDto> getParticipant(long eventId) {
        List<User> events = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        return Optional.ofNullable(events)
                .orElseGet(ArrayList::new)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
