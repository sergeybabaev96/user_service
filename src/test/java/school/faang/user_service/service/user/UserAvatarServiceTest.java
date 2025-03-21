package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.UserAvatarProperties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.TestUserAvatarProperties;
import school.faang.user_service.service.s3.S3Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {

    @Mock
    private UserAvatarProperties avatarProperties;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile avatarFile;

    private UserAvatarService userAvatarService;

    private final long userId = 1L;
    private final String bigImageKey = "bigAva" + userId;
    private final String smallImageKey = "smallAva" + userId;
    private User user;

    @BeforeEach
    void setUp() {
        avatarProperties = TestUserAvatarProperties.createTestProperties();
        userAvatarService = new UserAvatarService(userRepository, avatarProperties, s3Service);
        user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        MultipartFile avatarFile = mock(MultipartFile.class);

    }

    @Test
    void testUploadAvatarSuccess() throws Exception {
        Path path = Paths.get("src/test/resources/waterfall.png");
        byte[] imageBytes = Files.readAllBytes(path);
        MultipartFile avatar = new MockMultipartFile(
                "avatar", "avatar.jpg", "image/jpeg", imageBytes
        );
        long fileSize = avatar.getSize();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(s3Service).uploadFile(any(ByteArrayInputStream.class), eq(bigImageKey),
                eq(fileSize), eq("image/jpeg"));
        doNothing().when(s3Service).uploadFile(any(ByteArrayInputStream.class), eq(smallImageKey),
                anyLong(), eq("image/jpeg"));

        userAvatarService.uploadAvatar(userId, avatar);

        verify(s3Service, times(1)).uploadFile(any(ByteArrayInputStream.class), eq(bigImageKey),
                eq(fileSize), eq("image/jpeg"));
        verify(s3Service, times(1)).uploadFile(any(ByteArrayInputStream.class), eq(smallImageKey),
                anyLong(), eq("image/jpeg"));
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void testUploadAvatarUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userAvatarService.uploadAvatar(userId, avatarFile));
    }

    @Test
    void testGetAvatarByUserIdSuccess() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(bigImageKey);
        user.setUserProfilePic(userProfilePic);
        InputStream mockStream = new ByteArrayInputStream(new byte[13]);
        when(s3Service.downloadFile(bigImageKey)).thenReturn(mockStream);

        InputStream result = userAvatarService.getAvatarByUserId(userId);
        assertNotNull(result);
    }

    @Test
    void testGetAvatarByUserIdUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userAvatarService.getAvatarByUserId(userId));
    }

    @Test
    void testGetAvatarByUserIdAvatarNotFound() {
        user.setUserProfilePic(null);
        assertThrows(EntityNotFoundException.class, () -> userAvatarService.getAvatarByUserId(userId));
    }

    @Test
    void testDeleteAvatarSuccess() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(bigImageKey);
        userProfilePic.setSmallFileId(smallImageKey);
        user.setUserProfilePic(userProfilePic);

        userAvatarService.deleteAvatar(userId);

        verify(s3Service, times(2)).deleteFile(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteAvatarUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userAvatarService.deleteAvatar(userId));
    }

    @Test
    void testDeleteAvatarNotFound() {
        user.setUserProfilePic(null);
        assertThrows(EntityNotFoundException.class, () -> userAvatarService.deleteAvatar(userId));
    }
}

