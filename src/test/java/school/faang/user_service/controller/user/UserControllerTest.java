package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"domain.path=/api/v1"})
class UserControllerTest {

    private static final String BASE_URL = "/api/v1/users";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("domain.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .addPlaceholderValue("domain.path", "/api/v1")
                .build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserRegisterDto userRegisterDto = getUserRegisterDto();
        when(userService.registerUser(eq(userRegisterDto))).thenReturn(
                UserResponseRegisterDto.builder().email("email").build()
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andDo(print())
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPremiumUsersByFilter() throws Exception {
        UserFilterDto filters = new UserFilterDto(1L, true);
        UserDto user = UserDto.builder().id(1L).build();
        when(userService.getPremiumUsersByFilters(eq(1), eq(5), eq(filters))).thenReturn(List.of(user));

        mockMvc.perform(get(BASE_URL + "/page/{pageNumber}/size/{pageSize}/premium", 1, 5)
                        .param("countryId", "1")
                        .param("active", "true"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllUsersByFilter() throws Exception {
        UserFilterDto filters = new UserFilterDto(1L, true);
        UserDto user = UserDto.builder().id(1L).build();
        when(userService.getAllUsersByFilters(eq(1), eq(5), eq(filters))).thenReturn(List.of(user));

        mockMvc.perform(get(BASE_URL + "/page/{pageNumber}/size/{pageSize}", 1, 5)
                        .param("countryId", "1")
                        .param("active", "true"))
                .andExpect(status().isOk());
    }

    private UserRegisterDto getUserRegisterDto() {
        return UserRegisterDto.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .build();
    }
}