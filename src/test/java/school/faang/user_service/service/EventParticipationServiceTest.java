package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.ParticipantValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapperImpl userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipantValidator participantValidator;


    @Test
    public void testRegisterWithRegisteredUser() {
        long eventId = 1L;
        long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);

        doThrow(new BusinessException("Пользователь уже зарегистрирован на событие!")).when(participantValidator)
                .checkParticipantAlreadyRegistered(eventId, userId);

        assertThrows(BusinessException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId)
        );

        verify(eventParticipationRepository, never()).register(eventId, userId);
    }


    @Test
    public void testRegistrationUser() {
        long eventId = 1L;
        long userId = 1L;

        doNothing().when(participantValidator).checkParticipantAlreadyRegistered(eventId, userId);

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .register(eventId, userId);
    }


    @Test
    public void testUnregisterWithUnregisteredUser() {
        long eventId = 1L;
        long userId = 1L;

        doThrow(new BusinessException("Пользователь не зарегистрирован на событие!")).when(participantValidator)
                .checkParticipantNotRegistered(eventId, userId);

        assertThrows(BusinessException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId)
        );
    }


    @Test
    public void testUnregisterUser() {
        long eventId = 1L;
        long userID = 1L;
        User existingUser = new User();
        existingUser.setId(userID);

        doNothing().when(participantValidator).checkParticipantNotRegistered(eventId, userID);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(existingUser));

        eventParticipationRepository.findAllParticipantsByEventId(eventId);
        eventParticipationService.unregisterParticipant(eventId, userID);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userID);
    }


    @Test
    public void testGetParticipantUsers() {
        long eventId = 1L;

        User userFirst = new User();
        userFirst.setId(1L);
        userFirst.setUsername("John");
        userFirst.setEmail("John@gmail.com");

        User userSecond = new User();
        userSecond.setId(2L);
        userSecond.setUsername("Jane");
        userSecond.setEmail("Jane@gmail.com");

        List<User> users = List.of(userFirst, userSecond);
        List<UserReadDto> userDtos = List.of(
                UserReadDto.builder().id(1L).username("John").email("Doe").build(),
                UserReadDto.builder().id(2L).username("Jane").email("Smith").build()
        );

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        when(userMapper.toDto(userFirst))
                .thenReturn(UserReadDto.builder().id(1L).username("John").email("Doe").build());
        when(userMapper.toDto(userSecond))
                .thenReturn(UserReadDto.builder().id(2L).username("Jane").email("Smith").build());


        List<UserReadDto> result = eventParticipationService.getParticipant(eventId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getUsername());
        assertEquals("Jane", result.get(1).getUsername());

        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
    }


    @Test
    public void testGetParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 10;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);
        assertEquals(expectedCount, actualCount);

        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }
}



