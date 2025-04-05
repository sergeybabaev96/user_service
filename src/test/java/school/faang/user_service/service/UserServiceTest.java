package school.faang.user_service.service;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.CsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@NoArgsConstructor
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    CountryRepository countryRepository;

    @Mock
    PasswordService passwordService;

    @Spy
    CsvMapper csvMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                csvMapper,
                userMapper,
                countryRepository,
                passwordService,
                10,
                6
        );

    }

    @Test
    @DisplayName("Negative: error when user not found")
    void testFindUserNegativeNoUser() {
        long idForSearch = 1L;
        when(userRepository.findById(idForSearch)).thenReturn(Optional.empty());

        var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(idForSearch)
        );

        assertEquals(exception.getMessage(), String.format("User with id = %d doesn't exist", idForSearch));
    }

    @Test
    @DisplayName("Positive: successful find user by id")
    void testFindUserSuccess() {
        User user = createUser(1L);
        UserDto userDto = createUserDto(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.findUserById(user.getId());

        assertEquals(userDto, result);
        assertEquals(user.getId(), result.id());
    }

    @Test
    @DisplayName("Negative: no users found by ids")
    void testGetUsersByIdsNegativeNoUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);

        when(userRepository.findAllById(ids)).thenReturn(Collections.emptyList());
        List<UserDto> users = userService.getUsersByIds(ids);

        assertEquals(0, users.size());
    }

    @Test
    @DisplayName("Positive: successful find users by ids")
    void testGetUsersByIdsSuccess() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> users = createUsers(ids);
        List<UserDto> userDto = createUserDtos(users);

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenAnswer(input -> {
            User user = input.getArgument(0);
            return createUserDto(user.getId());
        });
        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(userDto, result);
    }

    @Test
    public void positiveRegisterUsersFromFile() {
        MockMultipartFile file;
        file = createValidCsvFile();
        when(passwordService.generateRandomPassword(anyInt()))
                .thenReturn("mockedPassword123");

        User savedUser = new User();
        savedUser.setUsername("John Doe");
        savedUser.setEmail("johndoe@example.com");

        UserDto expectedUserDto = UserDto.builder()
                .username("John Doe")
                .email("johndoe@example.com")
                .build();

        when(userMapper.toDto(any(User.class))).thenReturn(expectedUserDto);
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        List<UserDto> result = userService.registerUserFromFile(file);

        UserDto userDto = result.get(0);

        verify(passwordService).generateRandomPassword(10);
        verify(userRepository).save(any(User.class));

        assertEquals("John Doe", userDto.username());
        assertEquals("johndoe@example.com", userDto.email());
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void negativeRegisterUsersEmptyFile() {
        String csvContent = """
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csvContent.getBytes()
        );

        assertThrows(DataValidationException.class, () -> userService.registerUserFromFile(file));
    }

    @Test
    public void negativeRegisterUsersIncorrectDataLength() {
        String csvContent = "username,email,age\nJohn Doe,john@example.com,30";
        MockMultipartFile file = new MockMultipartFile("file", "users.csv",
                "text/csv", csvContent.getBytes());

        CompletionException completionException = assertThrows(
                CompletionException.class,
                () -> userService.registerUserFromFile(file).wait()
        );

        assertInstanceOf(DataValidationException.class, completionException.getCause());
    }


    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private List<User> createUsers(List<Long> ids) {
        return ids.stream()
                .map(this::createUser)
                .toList();
    }

    private UserDto createUserDto(Long id) {
        return new UserDto(id, "test", "test");
    }

    private List<UserDto> createUserDtos(List<User> users) {
        return users.stream()
                .map(user -> createUserDto(user.getId()))
                .toList();
    }

    private MockMultipartFile createValidCsvFile() {
        String csvContent = """
                John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies;
                """;

        return new MockMultipartFile(
                "file",
                "valid_users.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
    }

}
