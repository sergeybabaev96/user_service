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
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatarGenerator.AvatarGeneratorService;
import school.faang.user_service.service.externalStorage.S3Service;
import school.faang.user_service.validator.CreateUserValidator;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

    @InjectMocks
    private UserServiceImpl userService;

    long userId;

    @BeforeEach
    public void init() {
        userId = 10L;

        ReflectionTestUtils.setField(
                userService,
                "userAvatarsAwsFolder",
                TEST_USER_AVATARS_AWS_FOLDER);
    }

    @Test
    public void testGetUserById_UserIsFound_ReturnsUser() {
        var testUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var result = userService.getUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(testUser, result);
    }

    @Test
    public void testGetUserById_UserIsNotFound_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testCreateUser_FailedUsernameValidation_Throws() {
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
    public void testCreateUser_FailedUserEmailValidation_Throws() {
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
    public void testCreateUser_FailedCountryTitleValidation_Throws() {
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
    public void testCreateUser_FailedSaveUser_Throws() {
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
        when(s3Service.uploadFile(any(), any(long.class), any(), any(), any()))
                .thenReturn(externalResourceDto);

        var errorMessage = "Cannot save user to repository";
        when(userRepository.save(any())).thenThrow(new RuntimeException(errorMessage));

        // Act + Assert
        assertThrowsExactly(
                RuntimeException.class,
                () -> userService.createUser(requestDto),
                errorMessage);
        verify(s3Service, times(1)).deleteFile(externalResourceKey);
    }

    @Test
    public void testCreateUser_SaveUser_ReturnsUserDto() {
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
        when(s3Service.uploadFile(any(), any(long.class), any(), any(), any()))
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

        verify(s3Service, times(0)).deleteFile(externalResourceKey);
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