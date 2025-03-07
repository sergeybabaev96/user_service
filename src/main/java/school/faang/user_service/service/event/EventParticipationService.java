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
        if (users == null || users.stream()
                .noneMatch(user -> user.getId().equals(userId))) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new RuntimeException("Пользователь уже зарегистрирован на событие");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (users.stream()
                .anyMatch(user -> user.getId().equals(userId))) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("Пользователь {} успешно зарегистрирован на событие {}", userId, eventId);
        } else {
            throw new RuntimeException("Пользователь уже зарегистрирован на событие");
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
