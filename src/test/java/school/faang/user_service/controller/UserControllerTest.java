package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.CsvParseException;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private UserDto userDto;
    private List<UserDto> userDtos;
    private List<Long> userIds;
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private String csvContent = """
            firstName,lastName,email,phone
            John,Doe,john@example.com,1234567890
            Jane,Doe,jane@example.com,0987654321
            """;

    private MockMultipartFile file = new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            csvContent.getBytes()
    );

    private Map<String, String> mockResponse = Map.of(
            "JohnDoe", "email already exist",
            "JaneDoe", "success"
    );

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        userDto = UserDto.builder().id(1L).username("Join").build();
        userDtos = List.of(userDto, UserDto.builder().id(2L).username("Bob").build());
        userIds = List.of(1L, 2L);
        objectMapper = new ObjectMapper();

    }

    @Test
    public void testGetUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("Join")));
        verify(userService, times(1)).getUser(1L);
    }

    @Test
    public void testGetUsersByIds() throws Exception {
        when(userService.getUsersByIds(userIds)).thenReturn(userDtos);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("Join")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("Bob")));
        verify(userService, times(1)).getUsersByIds(userIds);
    }

    @Test
    public void testUploadUsersAndReturnResponse() throws Exception {
        when(userService.uploadUsersFromCsv(file)).thenReturn(mockResponse);

        mockMvc.perform(multipart("/users/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.JohnDoe").value("email already exist"))
                .andExpect(jsonPath("$.JaneDoe").value("success"));
        verify(userService, times(1)).uploadUsersFromCsv(file);
    }

    @Test
    public void testUploadCsvParseException() throws Exception {
        doThrow(new CsvParseException("any", new Throwable()))
                .when(userService).uploadUsersFromCsv(file);

        mockMvc.perform(multipart("/users/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CsvParseException"))
                .andExpect(status().isBadRequest());
    }
}
