package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import school.faang.user_service.dto.event.participant.EventParticipationDto;
import school.faang.user_service.dto.event.participant.UserParticipationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.partcipation.EventParticipationMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipationMapper eventParticipationMapper;

    private EventParticipationDto eventDto;
    private UserParticipationDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        eventDto = new EventParticipationDto(1L);
        userDto = new UserParticipationDto(1L);
    }

    @Test
    void registerParticipation_success() throws DataValidationException {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()))
                .thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipation(eventDto, userDto);
        verify(eventParticipationRepository).register(eventDto.id(), userDto.id());
    }

    @Test
    public void testRegisterParticipation_UserAlreadyRegistered() {
        User registeredUser = new User();
        registeredUser.setId(userDto.id());
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()))
                .thenReturn(Collections.singletonList(registeredUser));
        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipation(eventDto, userDto);
        });
    }

    @Test
    void unregisterParticipation_userRegistered() throws DataValidationException {
        User registeredUser = new User();
        registeredUser.setId(userDto.id());
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()))
                .thenReturn(Collections.singletonList(registeredUser));
        eventParticipationService.unregisterParticipation(eventDto, userDto);
        verify(eventParticipationRepository).unregister(eventDto.id(), userDto.id());
    }

    @Test
    void unregisterParticipation_userNotRegistered() throws DataValidationException {
        User registeredUser = new User();
        registeredUser.setId(userDto.id());
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()));
        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipation(eventDto, userDto);
            verify(eventParticipationRepository, never()).unregister(anyLong(), anyLong());
        });
    }

    @Test
    void getParticipantCount() throws DataValidationException {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<User> participants = Arrays.asList(user1, user2);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()))
                .thenReturn(participants);

        int result = eventParticipationService.getParticipantCount(eventDto);
        assertEquals(2, result);
        assertEquals(1L, result);
        assertEquals(2L, result);
    }

    @Test
    void getParticipant() throws DataValidationException {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<User> participants = Arrays.asList(user1, user2);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()))
                .thenReturn(participants);
        List<UserParticipationDto> result = eventParticipationService.getParticipant(eventDto);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }
}