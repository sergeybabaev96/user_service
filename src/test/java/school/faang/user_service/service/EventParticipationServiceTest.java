package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    private static final long USER_ID = 1L;
    private static final long EVENT_ID = 1L;

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .id(USER_ID)
                .username("testUser")
                .email("test@example.com")
                .phone("1234567890")
                .password("securePassword")
                .active(true)
                .aboutMe("About me")
                .city("Test City")
                .experience(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .participatedEvents(new ArrayList<>())
                .build();

        userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

    }

    @Test
    public void testRegisterParticipantUserNotRegisteredSuccess() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());

        eventParticipationService.registerParticipant(USER_ID, EVENT_ID);

        verify(eventParticipationRepository, times(1)).register(EVENT_ID, USER_ID);
    }

    @Test
    public void testRegisterParticipantUserAlreadyRegistered() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(user));

        BusinessException exception = Assert.assertThrows(BusinessException.class, () -> {
            eventParticipationService.registerParticipant(USER_ID, EVENT_ID);
        });

        Assertions.assertEquals(String.format("Пользователь с id: <%d> уже зарегистрирован на событие c id: <%d>", USER_ID, EVENT_ID), exception.getMessage());

    }

    @Test
    public void testUnregisterParticipantUserIsRegisteredSuccess() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);

        verify(eventParticipationRepository, times(1)).unregister(EVENT_ID, USER_ID);
    }

    @Test
    public void testUnregisterParticipantInvalidUserId() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);
        });

        Assertions.assertEquals(String.format("Пользователь с id: <%d> НЕ ЗАРЕГИСТРИРОВАН на событие c id: <%d>", USER_ID, EVENT_ID), exception.getMessage());
    }

    @Test
    public void testRegisterParticipantWithExistingUser() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(user));

        List<UserDto> result = eventParticipationService.getParticipant(EVENT_ID);

        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
        verify(userMapper, times(1)).toDto(user);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(userDto, result.get(0));
    }

    @Test
    public void testGetParticipantEmptyParticipantList() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());

        eventParticipationService.getParticipant(EVENT_ID);

    }

    @Test
    public void testGetParticipantCount() {
        when(eventParticipationRepository.countParticipants(EVENT_ID))
                .thenReturn(1);

        int count = eventParticipationService.getParticipantsCount(EVENT_ID);

        Assertions.assertEquals(1, count);
    }

}
