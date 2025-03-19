package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private final long eventId = 1L;
    private final long userId = 1L;
    private final int countInvocation = 1;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Spy
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void positiveRegisterParticipant() {
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository, times(countInvocation)).register(eventId, userId);
    }

    @Test
    void negativeUserAlreadyRegistered() {
        user.setId(userId);
        List<User> participants = List.of(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));
        assertEquals("Пользователь уже зарегистрирован на событие", exception.getMessage());
    }

    @Test
    void positiveUnregisterParticipant() {
        user.setId(userId);
        List<User> participants = List.of(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventParticipationRepository, times(countInvocation)).unregister(eventId, userId);
    }

    @Test
    void negativeUserNotRegistered() {
        user.setId(0L);
        List<User> participants = List.of(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
        assertEquals("Пользователь не участвует в событии", exception.getMessage());
    }

    @Test
    void positiveGetParticipant() {
        user.setId(userId);
        UserDto userDto = UserDto.builder()
                .id(userId)
                .build();
        List<User> participants = List.of(user);
        List<UserDto> participantDtos = List.of(userDto);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);
        when(userMapper.usersToUserDtos(participants)).thenReturn(participantDtos);

        assertEquals(participantDtos, eventParticipationService.getParticipant(eventId));
    }

    @Test
    void positiveGetParticipantsCount() {
        eventParticipationService.getParticipantsCount(eventId);
        verify(eventParticipationRepository, times(countInvocation)).countParticipants(eventId);
    }
}