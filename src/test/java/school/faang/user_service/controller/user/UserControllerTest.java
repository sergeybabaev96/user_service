package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    void getUser_ReturnsValidResponseDto() throws Exception {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toViewDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void getUsersByIds_ReturnsValidResponseDtoList() throws Exception {
        Mockito.when(userRepository.findAllById(ids)).thenReturn(users);
        Mockito.when(userMapper.toViewDto(user)).thenReturn(userDto);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(
                 objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()));
    }
}