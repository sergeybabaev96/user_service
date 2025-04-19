package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    private final User user = new User();
    private final User anotherUser = new User();
    private final UserViewDto userDto = new UserViewDto();
    private List<User> users;
    private List<Long> ids;

    @BeforeEach
    void setUp() {
        user.setId(1L);
        anotherUser.setId(2L);
        userDto.setId(user.getId());
        ids = List.of(user.getId(), anotherUser.getId());
        users = List.of(user);
    }

    @Test
    @DisplayName("Проверка успешного получения ответа на запрос по получению данных о пользователе по его id")
    void givenUserId_WhenGetUser_ThenReturnUser() throws Exception {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toViewDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного получения ответа на запрос по получению данных о пользователях по их id")
    void givenUsersIdsList_WhenGetUsersByIds_ThenReturnsUsers() throws Exception {
        Mockito.when(userRepository.findAllById(ids)).thenReturn(users);
        Mockito.when(userMapper.toViewDto(user)).thenReturn(userDto);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());
    }
}