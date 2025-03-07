package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isUserNotRegistered = users == null || users.stream()
                .noneMatch(user -> user.getId().equals(userId));
        if (isUserNotRegistered) {
            eventParticipationRepository.register(eventId, userId);
            log.info("Пользователь {} успешно зарегистрирован на событие {}", userId, eventId);
        } else {
            throw new RuntimeException("Пользователь уже зарегистрирован на событие");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean isUserRegistered = users.stream()
                .anyMatch(user -> user.getId().equals(userId));
        if (isUserRegistered) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("Пользователь {} отменил регистрацию на событие {}", userId, eventId);
        } else {
            throw new RuntimeException("Пользователь не участвует в событии");
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.usersToUserDtos(users);
    }

    public long getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
