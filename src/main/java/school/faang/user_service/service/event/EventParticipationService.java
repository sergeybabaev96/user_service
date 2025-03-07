package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final EventValidator eventValidator;

    @Transactional
    public void registerParticipant(Long eventId, Long userId) {
        validateDateById(eventId, userId);

        if (checkExistsByEventIdAndUserId(eventId, userId)) {
            log.warn("User with id {} is already registered for the event with id {}", userId, eventId);
            throw new IllegalArgumentException("User is already registered for the event");
        }

        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(Long eventId, Long userId) {
        validateDateById(eventId, userId);

        if (!checkExistsByEventIdAndUserId(eventId, userId)) {
            log.warn("User with id {} is not registered for the event with id {}", userId, eventId);
            throw new IllegalArgumentException("User is not registered for the event");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(Long eventId) {
        eventValidator.checkEventExistsById(eventId);
        return userMapper.toListUserDto(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    public Integer getParticipantCount(Long eventId) {
        eventValidator.checkEventExistsById(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void validateDateById(Long eventId, Long userId) {
        userValidator.checkUserExistsById(userId);
        eventValidator.checkEventExistsById(eventId);
    }

    private boolean checkExistsByEventIdAndUserId(Long eventId, Long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}
