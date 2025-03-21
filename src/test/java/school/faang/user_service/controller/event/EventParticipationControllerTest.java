package school.faang.user_service.controller.event;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

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
    void registerParticipation_InvalidId() throws Exception {
        this.mockMvc.perform(put("/events/-123/users/-456/register"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Id is less than zero"));

        verify(eventParticipationService, never()).registerParticipant(anyLong(), anyLong());
    }

    @Test
    void unregisterParticipant() throws Exception {
        doNothing().when(eventParticipationService).unregisterParticipant(123L, 456L);

        this.mockMvc.perform(delete("/events/123/users/456/unregister"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(eventParticipationService).unregisterParticipant(123L, 456L);
    }

    @Test
    void unregisterParticipant_InvalidId() throws Exception {
        this.mockMvc.perform(delete("/events/-123/users/-456/unregister"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Id is less than zero"));

        verify(eventParticipationService, never()).unregisterParticipant(anyLong(), anyLong());
    }
    
    @Test
    void getParticipants() throws Exception {
        List<UserDto> participants = List.of(
                UserDto.builder().id(1L).username("User1").build(),
                UserDto.builder().id(2L).username("User2").build()
        );

        when(eventParticipationService.getParticipants(123L)).thenReturn(participants);

        this.mockMvc.perform(get("/events/123/participants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Проверяем, что вернулось 2 участника
                .andExpect(jsonPath("$[0].id", is(1))) // Проверяем ID первого участника
                .andExpect(jsonPath("$[0].username", is("User1"))) // Проверяем имя первого участника
                .andExpect(jsonPath("$[1].id", is(2))) // Проверяем ID второго участника
                .andExpect(jsonPath("$[1].username", is("User2"))); // Проверяем имя второго участника

        verify(eventParticipationService).getParticipants(123L);
    }

    @Test
    void getParticipants_InvalidId() throws Exception {
        this.mockMvc.perform(get("/events/-123/participants"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Id is less than zero"));

        verify(eventParticipationService, never()).getParticipants(anyLong());
    }

    @Test
    void getParticipantsCount() throws Exception {
        when(eventParticipationService.getParticipantsCount(123L)).thenReturn(5);

        this.mockMvc.perform(get("/events/123/count_participants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("5")); // Проверяем, что возвращается число 5

        verify(eventParticipationService).getParticipantsCount(123L);
    }

    @Test
    void getParticipantsCount_InvalidId() throws Exception {
        this.mockMvc.perform(get("/events/-123/count_participants"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Id is less than zero"));

        verify(eventParticipationService, never()).getParticipantsCount(anyLong());
    }
}