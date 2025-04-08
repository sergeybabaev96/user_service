package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExternalResourceNotFoundException;
import school.faang.user_service.exception.ExternalServiceError;
import school.faang.user_service.service.UserService;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    public static final String TEST_USERNAME = "Dummy username";
    public static final String TEST_EMAIL = "example@mail.com";
    public static final String TEST_PASSWORD = "Dummy password";
    public static final String TEST_COUNTRY_TITLE = "Dummy country title";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser_CreateUserThrowsDataValidationException_Returns400() throws Exception {
        var requestDto = new CreateUserDto(
                "Already existed username",
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        when(userService.createUser(requestDto))
                .thenThrow(new DataValidationException("Username is already existed"));
        var requestJson = convertToJson(requestDto);

        mockMvc.perform(callCreateUserEndpoint(requestJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("getFailedDependencyReasonExceptions")
    public void testCreateUser_CreateUserThrowsExternalResourceNotFoundException_Returns424(Exception exception)
            throws Exception {
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        when(userService.createUser(requestDto)).thenThrow(exception);
        var requestJson = convertToJson(requestDto);

        mockMvc.perform(callCreateUserEndpoint(requestJson))
                .andExpect(status().isFailedDependency());
    }

    private static Stream<Arguments> getFailedDependencyReasonExceptions() {
        return Stream.of(
                Arguments.of(new ExternalResourceNotFoundException("External resource is not found")),
                Arguments.of(new ExternalServiceError("External server error")));
    }

    @Test
    public void testCreateUser_CreateUserThrowsException_Returns500() throws Exception {
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        when(userService.createUser(requestDto))
                .thenThrow(new RuntimeException("Unexpected exception"));
        var requestJson = convertToJson(requestDto);

        mockMvc.perform(callCreateUserEndpoint(requestJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreateUser_CreateUserReturnsUserDto_Returns201() throws Exception {
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        var expectedResult = new UserDto(1L, TEST_USERNAME, TEST_EMAIL, "Test file id");
        when(userService.createUser(requestDto)).thenReturn(expectedResult);
        var requestJson = convertToJson(requestDto);

        mockMvc.perform(callCreateUserEndpoint(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResult.id()))
                .andExpect(jsonPath("$.username").value(expectedResult.username()))
                .andExpect(jsonPath("$.email").value(expectedResult.email()))
                .andExpect(jsonPath("$.fileId").value(expectedResult.fileId()));
    }

    private static MockHttpServletRequestBuilder callCreateUserEndpoint(String requestJson) {
        return post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
    }

    private static String convertToJson(CreateUserDto requestDto) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(requestDto);
    }
}