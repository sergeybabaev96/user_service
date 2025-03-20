package school.faang.user_service.controller.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventParticipationController).build();
    }

    @Test
    void registerParticipation() throws Exception {
        doNothing().when(eventParticipationService).registerParticipant(123L, 456L);

        this.mockMvc.perform(put("/events/123/users/456/register"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(eventParticipationService).registerParticipant(123L, 456L);
    }

    @Test
    void registerParticipant_ValidData_Success() {
        long eventId = 123L;
        long userId = 456L;
        eventParticipationController.registerParticipant(eventId, userId);
        verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
    }

    @Test
    void registerParticipant_Invalid_ThrowsException() {
        // Arrange
        long eventId = -1L;
        long userId = 2L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventParticipationController.registerParticipant(eventId, userId);
        });

        assertEquals("Id is less than zero", exception.getMessage());
        verify(eventParticipationService, never()).registerParticipant(anyLong(), anyLong());
    }

    @Test
    void unregisterParticipation() throws Exception {
        doNothing().when(eventParticipationService).unregisterParticipant(123L, 456L);

        this.mockMvc.perform(delete("/events/123/users/456/unregister"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(eventParticipationService).unregisterParticipant(123L, 456L);
    }

    @Test
    void unregisterParticipant_ValidData_Success() {
        long eventId = 123L;
        long userId = 456L;
        eventParticipationController.unregisterParticipant(eventId, userId);
        verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
    }

    @Test
    void unregisterParticipant_Invalid_ThrowsException() {
        long eventId = -1L;
        long userId = 2L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventParticipationController.unregisterParticipant(eventId, userId);
        });

        assertEquals("Id is less than zero", exception.getMessage());
        verify(eventParticipationService, never()).unregisterParticipant(anyLong(), anyLong());
    }

    @Test
    void getParticipants_Success() {
        long eventId = 1L;

        UserDto secondUser = UserDto.builder()
                .username("John")
                .id(0L)
                .build();
        UserDto firstUser = UserDto.builder()
                .username("Lopux")
                .id(1L)
                .build();

        List<UserDto> participants = Arrays.asList(secondUser, firstUser);
        when(eventParticipationService.getParticipants(eventId)).thenReturn(participants);
        List<UserDto> result = eventParticipationController.getParticipants(eventId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getUsername());
        assertEquals("Lopux", result.get(1).getUsername());

        verify(eventParticipationService, times(1)).getParticipants(eventId);
    }

    @Test
    void getParticipants_InvalidId() {
        long invalidEventId = -1L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventParticipationController.getParticipants(invalidEventId);
        });

        assertEquals("Id is less than zero", exception.getMessage());
        verify(eventParticipationService, never()).getParticipants(anyLong());
    }

    @Test
    void getParticipantsCount() throws Exception {
        long eventId = 1L;
        int expectedCount = 5;
        when(eventParticipationService.getParticipantsCount(eventId)).thenReturn(expectedCount);

        mockMvc.perform(get("/events/{eventId}/count_participants", eventId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));

        verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
    }

    @Test
    void getParticipantsCount_InvalidId() {
        long eventId = -1L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventParticipationController.getParticipantsCount(eventId);
        });

        assertEquals("Id is less than zero", exception.getMessage());
        verify(eventParticipationService, never()).getParticipantsCount(anyLong());

    }
}