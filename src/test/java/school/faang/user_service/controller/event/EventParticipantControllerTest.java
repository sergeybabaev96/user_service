package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.event.EventParticipantService;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"domain.path=/api/v1"})
class EventParticipantControllerTest {

    private static final String BASE_URL = "/api/v1/event-participants";

    private MockMvc mockMvc;

    @Mock
    private EventParticipantService eventParticipantService;

    @InjectMocks
    private EventParticipantController eventParticipantController;

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("domain.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(eventParticipantController)
                .addPlaceholderValue("domain.path", "/api/v1")
                .build();
    }

    @Test
    public void testRegisterEventParticipant() throws Exception {
        doNothing().when(eventParticipantService).registerParticipant(eq(1L), eq(1L));

        mockMvc.perform(post(BASE_URL + "/register/event/{eventId}/user/{userId}", 1L, 1L))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUnregisterEventParticipant() throws Exception {
        doNothing().when(eventParticipantService).unregisterParticipant(eq(1L), eq(1L));

        mockMvc.perform(post(BASE_URL + "/unregister/event/{eventId}/user/{userId}", 1L, 1L))
                .andExpect(status().isNoContent());
    }

}