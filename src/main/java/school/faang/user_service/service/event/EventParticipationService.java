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

        if (users == null || !isUserRegistered(users, userId)) {
            eventParticipationRepository.register(eventId, userId);
            log.info("Пользователь {} успешно зарегистрирован на событие {}", userId, eventId);
        } else {
            throw new RuntimeException("Пользователь " + userId + " уже зарегистрирован на событие");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (isUserRegistered(users, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("Пользователь {} отменил регистрацию на событие {}", userId, eventId);
        } else {
            throw new RuntimeException("Пользователь " + userId + " не участвует в событии");
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.usersToUserDtos(users);
    }

    public long getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegistered(List<User> users, long userId) {
        return users.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}
