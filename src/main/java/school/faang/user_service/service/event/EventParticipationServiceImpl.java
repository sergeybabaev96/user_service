package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.ParticipantsCountDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventService eventService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public void registerParticipant(long eventId, long userId) {
        log.debug("Start to register user with id = {} to event with id = {}", userId, eventId);
        Event event = eventService.findByIdOrThrow(eventId);
        User user = userService.findByIdOrThrow(userId);

        if (isUserEventOwner(user, event)) {
            throw new DataValidationException(
                    String.format("User with id = %d is owner to event with id = %d. Cannot register", userId, eventId)
            );
        }
        if (isUserRegisteredToEvent(user, event)) {
            throw new DataValidationException(
                    String.format("User with id = %d already registered to event with id = %d", userId, eventId)
            );
        }

        eventParticipationRepository.register(event.getId(), user.getId());
        log.info("Registered user with id = {} to event with id = {}", userId, eventId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        log.debug("Start to unregister user with id = {} from event with id = {}", userId, eventId);
        Event event = eventService.findByIdOrThrow(eventId);
        User user = userService.findByIdOrThrow(userId);
        if (!isUserRegisteredToEvent(user, event)) {
            throw new DataValidationException(
                    String.format("User with id = %d is not registered to event with id = %d", userId, eventId)
            );
        }

        eventParticipationRepository.unregister(event.getId(), user.getId());
        log.info("Unregistered user with id = {} from event with id = {}", userId, eventId);
    }

    @Override
    public List<UserDto> getParticipants(long eventId) {
        Event event = eventService.findByIdOrThrow(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public ParticipantsCountDto getParticipantsCount(long eventId) {
        Event event = eventService.findByIdOrThrow(eventId);
        int participantsCount = eventParticipationRepository.countParticipants(eventId);

        return ParticipantsCountDto.builder()
                .eventId(eventId)
                .participantsCount(participantsCount)
                .build();
    }

    private boolean isUserEventOwner(User user, Event event) {
        return Objects.equals(user.getId(), event.getOwner().getId());
    }

    private boolean isUserRegisteredToEvent(User user, Event event) {
        return eventParticipationRepository.isUserRegisteredToEvent(event.getId(), user.getId());
    }

}
