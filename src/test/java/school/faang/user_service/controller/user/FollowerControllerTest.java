package school.faang.user_service.controller.user;

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
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.FollowerService;

import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"domain.path=/api/v1"})
class FollowerControllerTest {

    private static final String BASE_URL = "/api/v1/followers";

    private MockMvc mockMvc;

    @Mock
    private FollowerService followerService;

    @InjectMocks
    private FollowerController followerController;

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("domain.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(followerController)
                .addPlaceholderValue("domain.path", "/api/v1")
                .build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(followerService.getFollowersByUserId(eq(1L))).thenReturn(List.of(getFollowerDto()));

        mockMvc.perform(get(BASE_URL + "/{userId}", 1L))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(status().isOk());
    }

    private UserDto getFollowerDto() {
        return new UserDto(2L, null, null, null);
    }
}