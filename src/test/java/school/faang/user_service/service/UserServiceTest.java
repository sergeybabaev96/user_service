package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.FileData;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileSizeException;
import school.faang.user_service.exception.InvalidImageFormatException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final Long id = 1L;
    private final byte[] imageData = new byte[]{0x1, 0x2, 0x3};
    private final List<Long> ids = List.of(1L, 2L, 3L);

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private S3StorageService s3Service;

    @Mock
    private ImageCompressorService compressorService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Negative: error when user not found")
    void testFindUserNegativeNoUser() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(id)
        );

        assertEquals(exception.getMessage(), String.format("User with id = %d doesn't exist", id));
    }

    @Test
    @DisplayName("Positive: successful find user by id")
    void testFindUserSuccess() {
        User user = createUser(id);
        UserDto userDto = createUserDto(id);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.findUserById(user.getId());

        assertEquals(userDto, result);
        assertEquals(user.getId(), result.id());
    }

    @Test
    @DisplayName("Negative: no users found by ids")
    void testGetUsersByIdsNegativeNoUsers() {
        when(userRepository.findAllById(ids)).thenReturn(Collections.emptyList());
        List<UserDto> users = userService.getUsersByIds(ids);

        assertEquals(0, users.size());
    }

    @Test
    @DisplayName("Positive: successful find users by ids")
    void testGetUsersByIdsSuccess() {
        List<User> users = createUsers(ids);
        List<UserDto> userDtos = createUserDtos(users);

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenAnswer(input -> {
            User user = input.getArgument(0);
            return createUserDto(user.getId());
        });
        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(userDtos, result);
    }

    @Test
    void testNegativeCreateAvatarWhenFileTypeIncorrect() {
        MockMultipartFile imageFile = createImageFile("image/pdf", imageData);

        assertThrows(InvalidImageFormatException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testNegativeCreateAvatarWhenFileSizeIncorrect() {
        byte[] bytes = new byte[6 * 1024 * 1024];
        Arrays.fill(bytes, (byte) 0x1);
        MockMultipartFile imageFile = createImageFile("image/jpeg", bytes);

        assertThrows(FileSizeException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testNegativeCreateAvatarWhenUserNotFound() {
        MockMultipartFile imageFile = createImageFile("image/jpeg", imageData);

        assertThrows(UserNotFoundException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testPositiveCreateAvatar() {
        MockMultipartFile imageFile = createImageFile("image/jpeg", imageData);
        when(userContext.getUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(createUser(id)));

        userService.createUserAvatar(imageFile);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(userRepository, times(1)).save(any(User.class));
        verify(compressorService, times(2)).compressImage(
                any(MultipartFile.class),
                sizeCaptor.capture()
        );
        assertTrue(sizeCaptor.getAllValues().containsAll(List.of(1080, 170)));
    }

    @Test
    void testNegativeGetAvatarWhenAvatarNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUserAvatar(id));
    }

    @Test
    void testPositiveGetAvatar() throws IOException {
        String expectedFileName = "file";
        InputStream fixedInputStream = new ByteArrayInputStream(imageData);
        User user = createUserWithAvatar(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(s3Service.getFile(expectedFileName)).thenReturn(fixedInputStream);
        when(s3Service.getContentType(expectedFileName)).thenReturn("image/jpeg");

        FileData resultFile = userService.getUserAvatar(id);

        byte[] resultBytes = toByteArray(resultFile.content());
        assertArrayEquals(imageData, resultBytes);
        assertEquals("image/jpeg", resultFile.contentType());
    }

    @Test
    void testPositiveRemoveAvatar() {
        User user = createUserWithAvatar(id);
        when(userContext.getUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.removeUserAvatar();

        verify(s3Service, times(2)).deleteFile(anyString());
        verify(userRepository, times(1)).save(any(User.class));
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

    private MockMultipartFile createImageFile(String contentType, byte[] bytes) {
        return new MockMultipartFile(
                "file",
                "avatar.jpg",
                contentType,
                bytes
        );
    }

    private User createUserWithAvatar(Long id) {
        return User.builder()
                .id(id)
                .userProfilePic(UserProfilePic.builder()
                        .fileId("file")
                        .smallFileId("smallfile")
                        .build())
                .build();
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        try (inputStream) {
            return inputStream.readAllBytes();
        }
    }
}
