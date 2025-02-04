package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private final static long MOCKED_EVENT_ID = 2;
    private final static long MOCKED_USER_ID = 1;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    void testRegisterParticipantWhenItIsAlreadyIn() {
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder()
                        .id(MOCKED_USER_ID)
                        .build()));
        eventParticipationService.registerParticipant(MOCKED_USER_ID, MOCKED_EVENT_ID);

    }

    @Test
    void testRegisterParticipantInvocation() {
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of());
        eventParticipationService.registerParticipant(MOCKED_EVENT_ID, MOCKED_USER_ID);
        verify(
                eventParticipationRepository,
                times(1)
        ).register(MOCKED_EVENT_ID, MOCKED_USER_ID);
    }

    @Test
    void testUnregisterParticipantWhenUserNotInEvent() {
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of());
        assertThrows(
                ResponseStatusException.class,
                () -> eventParticipationService.unregister(MOCKED_EVENT_ID, MOCKED_USER_ID)
        );
    }

    @Test
    void testUnregisterParticipantInvocation() {
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder()
                        .id(MOCKED_USER_ID)
                        .build()));
        eventParticipationService.unregister(MOCKED_EVENT_ID, MOCKED_USER_ID);
        verify(
                eventParticipationRepository,
                times(1)
        ).unregister(MOCKED_EVENT_ID, MOCKED_USER_ID);
    }


    @Test
    void testGetRegisterParticipantListIfException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(MOCKED_EVENT_ID))
                .thenThrow(new RuntimeException("Exception"));
        assertThrows(
                RuntimeException.class,
                () -> eventParticipationService.getParticipant(MOCKED_EVENT_ID)
        );
    }

    @Test
    void testGetRegisterParticipantListInvocation() {
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder().id(MOCKED_USER_ID).build()));
        List<UserDto> users = eventParticipationService.getParticipant(MOCKED_EVENT_ID);
        verify(
                eventParticipationRepository,
                times(1)
        ).findAllParticipantsByEventId(MOCKED_EVENT_ID);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getId(), MOCKED_USER_ID);

    }

    @Test
    void testGetRegisterParticipantCountIfException() {
        when(eventParticipationRepository.countParticipants(anyLong()))
                .thenThrow(new RuntimeException("Exception"));
        assertThrows(
                RuntimeException.class,
                () -> eventParticipationService.getParticipantsCount(MOCKED_EVENT_ID)
        );
    }

    @Test
    void testGetRegisterParticipantCountInvocation() {
        when(eventParticipationRepository.countParticipants(anyLong()))
                .thenReturn(1);
        int participantsCount = eventParticipationService.getParticipantsCount(MOCKED_EVENT_ID);
        verify(
                eventParticipationRepository,
                times(1)
        ).countParticipants(MOCKED_EVENT_ID);
        assertEquals(participantsCount, 1);
    }
}