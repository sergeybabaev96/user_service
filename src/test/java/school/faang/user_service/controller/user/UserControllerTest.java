package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.user.UserCacheProfilePicDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserIdsDto;
import school.faang.user_service.entity.user_cache.UserCacheDto;
import school.faang.user_service.service.user.UserCacheService;
import school.faang.user_service.service.user.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void getUserByIdTest() throws Exception {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .username("username")
                .build();

        when(userService.getUser(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(userDto.getUsername())));
    }

    @Test
    public void getUsersByIdsTest() throws Exception {
        UserIdsDto request = new UserIdsDto(List.of(1L, 2L));
        List<UserDto> users = List.of(
                UserDto.builder().username("username1").build(),
                UserDto.builder().username("username2").build()
        );

        when(userService.getUsers(request.getUserIds())).thenReturn(users);

        String body = "{\"userIds\":[1, 2]}";

        mockMvc.perform(post("/users/by-ids")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("username1")))
                .andExpect(jsonPath("$[1].username", is("username2")));
    }

    @Test
    public void getUsersByIdsEmptyBodyTest() throws Exception {
        String emptyBody = "{\"userIds\":[]}";

        mockMvc.perform(post("/users/by-ids")
                        .contentType(APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUsersByIdsNullIdTest() throws Exception {
        String nullIdBody = "{\"userIds\":[1, null]}";

        mockMvc.perform(post("/users/by-ids")
                        .contentType(APPLICATION_JSON)
                        .content(nullIdBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNotPremiumUsersTest() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("Charlie")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("Dana")
                .build();

        UserFilterDto filters = new UserFilterDto();
        List<UserDto> notPremiumUsers = Arrays.asList(userDto1, userDto2);
        String filterJson = objectMapper.writeValueAsString(filters);

        when(userService.getNotPremiumUsers(any(UserFilterDto.class))).thenReturn(notPremiumUsers);

        mockMvc.perform(post("/users/not-premium")
                        .contentType(APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Charlie"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Dana"));
    }

    @Test
    void getPremiumUsersTest() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L).username("Charlie")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("Dana")
                .build();

        List<UserDto> premiumUsers = Arrays.asList(userDto1, userDto2);
        UserFilterDto filters = new UserFilterDto();
        String filterJson = objectMapper.writeValueAsString(filters);

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(premiumUsers);

        mockMvc.perform(post("/users/premium")
                        .contentType(APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Charlie"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Dana"));
    }

    @Test
    void getUserActiveStatusTest() throws Exception {
        Long firstUserId = 1L;
        Long secondUserId = 1L;

        when(userService.isUserActive(firstUserId)).thenReturn(true);

        mockMvc.perform(get("/users/active/%s".formatted(firstUserId))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(userService.isUserActive(secondUserId)).thenReturn(false);

        mockMvc.perform(get("/users/active/%s".formatted(secondUserId))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void isUserExistsTest() throws Exception {
        Long firstUserId = 1L;
        Long secondUserId = 1L;

        when(userService.existsById(firstUserId)).thenReturn(true);

        mockMvc.perform(get("/users/exists/%s".formatted(firstUserId))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(userService.existsById(secondUserId)).thenReturn(false);

        mockMvc.perform(get("/users/exists/%s".formatted(secondUserId))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getUsersCachesByIdsTest() throws Exception {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserCacheDto user1 = new UserCacheDto(1L, "user1", true, new UserCacheProfilePicDto());
        UserCacheDto user2 = new UserCacheDto(2L, "user2", false, new UserCacheProfilePicDto());

        List<UserCacheDto> mockResponse = Arrays.asList(user1, user2);

        when(userCacheService.getUsersCachesDtos(userIds)).thenReturn(mockResponse);

        mockMvc.perform(post("/users/caches")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    @Test
    void heatCacheTest() throws Exception {
        mockMvc.perform(post("/users/caches/heat")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Cache heating started successfully."));
    }
}

