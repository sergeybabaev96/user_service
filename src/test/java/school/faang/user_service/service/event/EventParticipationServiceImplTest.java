package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.ParticipantsCountDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.users.UsersService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationServiceImpl;
    @Mock
    private EventParticipationRepository eventParticipationRepositoryMock;
    @Mock
    private EventService eventServiceMock;
    @Mock
    private UsersService usersServiceMock;
    @Spy
    private UserMapperImpl userMapperMock;

    private User eventOwner;
    private Event event;
    private User registeredUser;
    private User unregisteredUser;

    @BeforeEach
    void setUp() {
        this.eventOwner = User.builder()
                .id(1L)
                .build();
        this.event = Event.builder()
                .id(1L)
                .owner(eventOwner)
                .build();
        this.registeredUser = User.builder()
                .id(2L)
                .build();
        this.unregisteredUser = User.builder()
                .id(3L)
                .build();
    }

    @Test
    void registerParticipantIfEventOrUserNotFoundThenThrowEntityNotFoundException() {
        long eventId = 666;
        long userId = 999;

        testMissedEvent(eventId);
        testMissedUser(userId);
    }

    @Test
    void registerParticipantIfUserIsEventOwnerThenThrowDataValidationException() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(usersServiceMock.findByIdOrThrow(eventOwner.getId()))
                .thenReturn(eventOwner);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventParticipationServiceImpl.registerParticipant(event.getId(), eventOwner.getId()));
    }

    @Test
    void registerParticipantIfUserAlreadyRegisteredThenThrowDataValidationException() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(usersServiceMock.findByIdOrThrow(registeredUser.getId()))
                .thenReturn(registeredUser);
        Mockito.when(eventParticipationRepositoryMock.isUserRegisteredToEvent(event.getId(), registeredUser.getId()))
                .thenReturn(Boolean.TRUE);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventParticipationServiceImpl.registerParticipant(event.getId(), registeredUser.getId()));
    }

    @Test
    void registerParticipant() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(usersServiceMock.findByIdOrThrow(unregisteredUser.getId()))
                .thenReturn(unregisteredUser);
        Mockito.when(eventParticipationRepositoryMock.isUserRegisteredToEvent(event.getId(), unregisteredUser.getId()))
                .thenReturn(Boolean.FALSE);

        eventParticipationServiceImpl.registerParticipant(event.getId(), unregisteredUser.getId());

        Mockito.verify(eventServiceMock, Mockito.times(1))
                .findByIdOrThrow(event.getId());
        Mockito.verify(usersServiceMock, Mockito.times(1))
                .findByIdOrThrow(unregisteredUser.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .isUserRegisteredToEvent(event.getId(), unregisteredUser.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .register(event.getId(), unregisteredUser.getId());
    }

    @Test
    void unregisterParticipantIfEventOrUserNotFoundThenThrowEntityNotFoundException() {
        long eventId = 111;
        long userId = 222;

        testMissedEvent(eventId);
        testMissedUser(userId);
    }

    @Test
    void unregisterParticipantIfUserIsNotRegisteredThenThrowDataValidationException() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(usersServiceMock.findByIdOrThrow(unregisteredUser.getId()))
                .thenReturn(unregisteredUser);
        Mockito.when(eventParticipationRepositoryMock.isUserRegisteredToEvent(event.getId(), unregisteredUser.getId()))
                .thenReturn(Boolean.FALSE);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventParticipationServiceImpl.unregisterParticipant(event.getId(), unregisteredUser.getId()));
    }

    @Test
    void unregisterParticipant() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(usersServiceMock.findByIdOrThrow(registeredUser.getId()))
                .thenReturn(registeredUser);
        Mockito.when(eventParticipationRepositoryMock.isUserRegisteredToEvent(event.getId(), registeredUser.getId()))
                .thenReturn(Boolean.TRUE);

        eventParticipationServiceImpl.unregisterParticipant(event.getId(), registeredUser.getId());

        Mockito.verify(eventServiceMock, Mockito.times(1))
                .findByIdOrThrow(event.getId());
        Mockito.verify(usersServiceMock, Mockito.times(1))
                .findByIdOrThrow(registeredUser.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .isUserRegisteredToEvent(event.getId(), registeredUser.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .unregister(event.getId(), registeredUser.getId());
    }

    @Test
    void getParticipantsIfEventNotFoundThenThrowEntityNotFoundException() {
        long eventId = 555;
        testMissedEvent(eventId);
    }

    @Test
    void getParticipants() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(eventParticipationRepositoryMock.findAllParticipantsByEventId(event.getId()))
                .thenReturn(List.of(registeredUser));

        List<UserDto> participants = eventParticipationServiceImpl.getParticipants(event.getId());

        Mockito.verify(eventServiceMock, Mockito.times(1))
                .findByIdOrThrow(event.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .findAllParticipantsByEventId(event.getId());

        Assertions.assertEquals(userMapperMock.toUserDto(registeredUser), participants.get(0));

    }

    @Test
    void getParticipantsCountIfEventNotFoundThenThrowEntityNotFoundException() {
        long eventId = 888;
        testMissedEvent(eventId);
    }

    @Test
    void getParticipantsCount() {
        Mockito.when(eventServiceMock.findByIdOrThrow(event.getId()))
                .thenReturn(event);
        Mockito.when(eventParticipationRepositoryMock.countParticipants(event.getId()))
                .thenReturn(1);

        ParticipantsCountDto participantsCountDto = eventParticipationServiceImpl.getParticipantsCount(event.getId());

        Mockito.verify(eventServiceMock, Mockito.times(1))
                .findByIdOrThrow(event.getId());
        Mockito.verify(eventParticipationRepositoryMock, Mockito.times(1))
                .countParticipants(event.getId());

        Assertions.assertEquals(1, participantsCountDto.participantsCount());
    }

    private void testMissedEvent(long eventId) {
        Mockito.when(eventServiceMock.findByIdOrThrow(eventId))
                .thenThrow(new EntityNotFoundException("Event is not exists! id: " + eventId));

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> eventParticipationServiceImpl.registerParticipant(eventId, Mockito.anyLong())
        );
    }

    private void testMissedUser(long userId) {
        Mockito.when(usersServiceMock.findByIdOrThrow(userId))
                .thenThrow(new EntityNotFoundException("User is not exists! id: " + userId));

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> eventParticipationServiceImpl.registerParticipant(Mockito.anyLong(), userId)
        );
    }

}
