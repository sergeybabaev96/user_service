package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private static final long eventId = 1L;
    private static final long userId = 1L;

    @Test
    public void testRegisterParticipantWhenUserNotRegistered() {

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .register(eventId, userId);
    }

    @Test
    public void testRegisterParticipantWhenUserAlreadyRegistered() {
        User user = new User();
        user.setId(userId);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });

        verify(eventParticipationRepository, never()).register(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantWhenUserRegistered() {
        User user = new User();
        user.setId(userId);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantWhenUserNotRegistered() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });

        verify(eventParticipationRepository, never()).unregister(eventId, userId);
    }

    @Test
    public void testGetParticipants() {
        User firstUser = User.builder()
                .id(1L)
                .username("Kicha")
                .email("kicha@gmail.com")
                .build();

        UserDto userDto1 = new UserDto(1L, "Kicha", "kicha@gmail.com");

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(firstUser));

        List<UserDto> result = eventParticipationService.getParticipants(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto1, result.get(0));
    }

    @Test
    public void testGetParticipants_EmptyList() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());
        List<UserDto> result = eventParticipationService.getParticipants(eventId);

        assertEquals(List.of(), result);
    }

    @Test
    public void testGetParticipantsCount() {
        int expectedCount = 1;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int result = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, result);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }
}
