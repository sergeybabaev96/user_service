package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatarGenerator.AvatarGeneratorService;
import school.faang.user_service.service.externalStorage.S3Service;
import school.faang.user_service.validator.CreateUserValidator;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    public static final String TEST_USER_AVATARS_AWS_FOLDER = "userAvatarsFolder";

    public static final String TEST_USERNAME = "Dummy username";
    public static final String TEST_EMAIL = "example@mail.com";
    public static final String TEST_PASSWORD = "Dummy password";
    public static final String TEST_COUNTRY_TITLE = "Dummy country title";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AvatarGeneratorService avatarGeneratorService;
    @Mock
    private S3Service s3Service;

    @Mock
    private CreateUserValidator createUserValidator;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Mock
    private UserContext userContext;

    @Mock
    private EventServiceImpl eventService;

    @Mock
    private GoalServiceImpl goalService;

    @Mock
    private MentorUserRelationHandlerImpl mentorshipService;

    @InjectMocks
    private UserServiceImpl userService;

    long userId;

    @BeforeEach
    public void init() {
        userId = 10L;

        ReflectionTestUtils.setField(userService, "userAvatarsAwsFolder", TEST_USER_AVATARS_AWS_FOLDER);
    }

    @Test
    public void testGetUser_invalidUserId_throws() {
        var userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    public void testFindUserById_userIsFound_returnsUser() {
        var testUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var result = userService.findUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(testUser, result);
    }

    @Test
    public void testFindUserById_userIsNotFound_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDeactivateUsers_throw_WhenUserIdIsNull() {
        when(userContext.getUserId()).thenThrow();
        Exception exception = assertThrows(DataValidationException.class, ()->
                userService.deactivateUser());
        assertEquals("User id cannot be null", exception.getMessage());
    }


    @Test
    public void testDeactivateUsers() {

        long userId = 1L;
        User user = User.builder().id(userId).active(true).build();
        User deactivatedUser = User.builder().id(userId).active(false).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);

        UserDto result = userService.deactivateUser();

        assertNotNull(result);
        verify(eventService).deleteEventByUserId(userId);
        verify(eventService).deleteParticipationFromEvent(userId);
        verify(goalService).deleteUserFromGoals(userId);
        verify(mentorshipService).deleteFromMentorShipDeactivatedUser(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(deactivatedUser);
        verify(userContext,times(1)).getUserId();
        assertFalse(deactivatedUser.isActive());
    }

    @Test
    public void testGetUser_userId_returnsUserDto() {
        var userId = 1L;
        var user = createTestUser(userId, "Test user name", "example@gmail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var result = userService.getUser(userId);

        assertEquals(user.getId(), result.id());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    public void testGetUsersByIds_emptyIds_returnsEmptyList() {
        List<Long> userIds = new ArrayList<>();
        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        var result = userService.getUsersByIds(userIds);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersByIds_severalIds_returnsNonEmptyList() {
        // Arrange
        List<Long> userIds = List.of(1L, 2L, 3L);
        var users = List.of(
                createTestUser(2L, "Test user name 1", "example1@gmail.com"),
                createTestUser(3L, "Test user name 2", "example2@gmail.com"));
        when(userRepository.findAllById(userIds)).thenReturn(users);

        // Act
        var result = userService.getUsersByIds(userIds);

        // Assert
        assertEquals(users.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(users.get(i).getId(), result.get(i).id());
            assertEquals(users.get(i).getUsername(), result.get(i).username());
            assertEquals(users.get(i).getEmail(), result.get(i).email());
        }
    }

    private static User createTestUser(long userId, String username, String email) {
        return User.builder()
                .id(userId)
                .username(username)
                .email(email)
                .build();
    }

    @Test
    public void testCreateUser_failedUsernameValidation_throws() {
        var requestDto = new CreateUserDto(
                "Already existed username",
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        var expectedErrorMessage = "Already existed username";
        doThrow(new DataValidationException(expectedErrorMessage))
                .when(createUserValidator)
                .validateUsername(requestDto);

        assertThrowsExactly(
                DataValidationException.class,
                () -> userService.createUser(requestDto),
                expectedErrorMessage);
    }

    @Test
    public void testCreateUser_failedUserEmailValidation_throws() {
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                "Already existed email",
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);
        var expectedErrorMessage = "Already existed email";
        doThrow(new DataValidationException(expectedErrorMessage))
                .when(createUserValidator)
                .validateUserEmail(requestDto);

        assertThrowsExactly(
                DataValidationException.class,
                () -> userService.createUser(requestDto),
                expectedErrorMessage);
    }

    @Test
    public void testCreateUser_failedCountryTitleValidation_throws() {
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                "Country title which is not existed");
        var expectedErrorMessage = "Country is not existed";
        when(createUserValidator.validateCountryTitle(requestDto))
                .thenThrow(new DataValidationException(expectedErrorMessage));

        assertThrowsExactly(
                DataValidationException.class,
                () -> userService.createUser(requestDto),
                expectedErrorMessage);
    }

    @Test
    public void testCreateUser_failedSaveUser_throws() {
        // Arrange
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);

        var country = new Country();
        country.setTitle(TEST_COUNTRY_TITLE);
        when(createUserValidator.validateCountryTitle(requestDto)).thenReturn(country);

        when(s3Service.getResourceKey(any(), any())).thenReturn("test file key");

        var errorMessage = "Cannot save user to repository";
        when(userRepository.save(any())).thenThrow(new RuntimeException(errorMessage));

        // Act + Assert
        assertThrowsExactly(
                RuntimeException.class,
                () -> userService.createUser(requestDto),
                errorMessage);
        verify(s3Service, never()).uploadFile(any(), any(long.class), any(), any(), any(), any());
    }

    @Test
    public void testCreateUser_saveUser_returnsUserDto() {
        // Arrange
        var requestDto = new CreateUserDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_COUNTRY_TITLE);

        var country = new Country();
        country.setTitle(TEST_COUNTRY_TITLE);
        when(createUserValidator.validateCountryTitle(requestDto)).thenReturn(country);

        var imageData = new byte[0];
        var bufferFactory = new DefaultDataBufferFactory();
        var imageBuffer = bufferFactory.allocateBuffer(imageData.length);
        when(avatarGeneratorService.getRandomAvatar()).thenReturn(imageBuffer);
        when(avatarGeneratorService.getRandomAvatarContentType()).thenReturn("");

        var externalResourceKey = "test-key";
        var externalResourceDto = createExternalResourceDto(externalResourceKey);
        when(s3Service.uploadFile(any(), any(long.class), any(), any(), any(), any()))
                .thenReturn(externalResourceDto);

        var userId = 1L;
        var userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(externalResourceKey);
        var savedUser = User.builder()
                .id(userId)
                .username(requestDto.username())
                .email(requestDto.email())
                .userProfilePic(userProfilePic)
                .build();
        when(userRepository.save(any())).thenReturn(savedUser);

        // Act
        var result = userService.createUser(requestDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        var capturedUser = userCaptor.getValue();

        assertEquals(TEST_USERNAME, capturedUser.getUsername());
        assertEquals(TEST_EMAIL, capturedUser.getEmail());
        assertEquals(TEST_PASSWORD, capturedUser.getPassword());
        assertEquals(TEST_COUNTRY_TITLE, capturedUser.getCountry().getTitle());

        assertEquals(userId, result.id());
        assertEquals(TEST_USERNAME, result.username());
        assertEquals(TEST_EMAIL, result.email());
        assertEquals(externalResourceKey, result.fileId());
    }

    private static ExternalResourceDto createExternalResourceDto(String key) {
        return new ExternalResourceDto(
                key,
                BigInteger.valueOf(1),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "",
                "test file.txt");
    }
}