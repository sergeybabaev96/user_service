package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.avatar.AvatarResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.avatar.AvatarType;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private DiceBearService diceBearService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAvatarService userAvatarService;

    private User user;
    private String randomFileName;
    private final int maxImageSize = 1024 * 1024;
    private final String customAvatarsStorage = "custom-avatars";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        randomFileName = "e9993c5c-8020-40d0-a965-f4c44485b3e5.jpg";
        ReflectionTestUtils.setField(userAvatarService, "maxImageSize", maxImageSize);
        ReflectionTestUtils.setField(userAvatarService, "largeImageSideSize", 1080);
        ReflectionTestUtils.setField(userAvatarService, "smallImageSideSize", 170);
        ReflectionTestUtils.setField(userAvatarService, "customAvatarsStorage", customAvatarsStorage);

    }

    @Test
    void testGenerateRandomAvatarCorrect() {
        byte[] avatarData = "avatar data".getBytes();

        when(diceBearService.generateAvatar(any(), any())).thenReturn(avatarData);
        when(s3Service.uploadToBucket(any(), any(), any(), any())).
                thenReturn(randomFileName);

        User updatedUser = userAvatarService.generateRandomAvatar(user, AvatarType.JPEG);

        assertEquals(updatedUser.getUserProfilePic().getFileId(), randomFileName);
        verify(diceBearService, times(1))
                .generateAvatar(any(), any());
        verify(s3Service, times(1))
                .uploadToBucket(any(), any(), any(), any());
    }

    @Test
    void testGetUserAvatarCorrect() {
        UserProfilePic profilePic = new UserProfilePic();
        String fileId = "test-file-id";
        profilePic.setFileId(fileId);
        user.setUserProfilePic(profilePic);
        String expectedUrl = "test-url";

        when(s3Service.getUnexpiredUrl(any(), any())).thenReturn(expectedUrl);

        String actualUrl = userAvatarService.getUserAvatar(user);

        assertEquals(expectedUrl, actualUrl);
        verify(s3Service, times(1)).getUnexpiredUrl(any(), any());
    }

    @Test
    void testGetUserAvatarNoAvatar() {
        assertThrows(NoSuchElementException.class, () -> userAvatarService.getUserAvatar(user));
    }

    @Test
    void testGetUserAvatar_blankAvatar() {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(" ");
        user.setUserProfilePic(profilePic);

        assertThrows(NoSuchElementException.class, () -> userAvatarService.getUserAvatar(user));
    }

    @Test
    void uploadAvatar_success() throws Exception {
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", byteStream);
        byte[] imageBytes = byteStream.toByteArray();

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", imageBytes);

        user.setId(userId);
        when(userService.getUser(userId)).thenReturn(user);

        when((s3Service).uploadToBucket(anyString(), anyString(), any(byte[].class), eq("image/jpeg")))
                .thenReturn(null);

        AvatarResponseDto response = userAvatarService.uploadAvatar(userId, multipartFile);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertNotNull(response.getLargeImageKey());
        assertNotNull(response.getSmallImageKey());
        assertTrue(response.getLargeImageKey().endsWith(".jpg"));
        assertTrue(response.getSmallImageKey().endsWith(".jpg"));

        verify(s3Service, times(1))
                .uploadToBucket(eq(customAvatarsStorage), eq(response.getLargeImageKey()), any(byte[].class), eq("image/jpeg"));
        verify(s3Service, times(1))
                .uploadToBucket(eq(customAvatarsStorage), eq(response.getSmallImageKey()), any(byte[].class), eq("image/jpeg"));

        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void uploadAvatar_invalidImageFormat_throwsException() throws Exception {
        byte[] invalidBytes = "not an image".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", invalidBytes);

        user.setId(userId);
        when(userService.getUser(userId)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userAvatarService.uploadAvatar(userId, multipartFile);
        });
        assertEquals("Invalid image format", exception.getMessage());
    }

    @Test
    void uploadAvatar_fileTooLarge_throwsException() throws Exception {
        byte[] dummyBytes = new byte[10];
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", dummyBytes) {
            @Override
            public long getSize() {
                return maxImageSize + 1;
            }
        };

        user.setId(userId);
        when(userService.getUser(userId)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userAvatarService.uploadAvatar(userId, multipartFile);
        });
        assertTrue(exception.getMessage().contains("File size must not be more than"));
    }

    @Test
    void getAvatar_success() {
        user.setId(userId);
        UserProfilePic profilePic = new UserProfilePic();
        String largeImageKey = "largeImageKey.jpg";
        profilePic.setFileId(largeImageKey);
        user.setUserProfilePic(profilePic);
        when(userService.getUser(userId)).thenReturn(user);

        String expectedUrl = "http://s3.fakeurl.com/" + largeImageKey;
        when(s3Service.getUnexpiredUrl(customAvatarsStorage, largeImageKey)).thenReturn(expectedUrl);

        String url = userAvatarService.getAvatar(userId);
        assertEquals(expectedUrl, url);
    }

    @Test
    void getAvatar_noAvatar_throwsException() {
        user.setId(userId);
        user.setUserProfilePic(null);
        when(userService.getUser(userId)).thenReturn(user);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userAvatarService.getAvatar(userId);
        });
        assertTrue(exception.getMessage().contains("has not set an avatar yet"));
    }

    @Test
    void deleteAvatar_success() {
        user.setId(userId);
        UserProfilePic profilePic = new UserProfilePic();
        String largeImageKey = "largeImageKey.jpg";
        String smallImageKey = "smallImageKey.jpg";
        profilePic.setFileId(largeImageKey);
        profilePic.setSmallFileId(smallImageKey);
        user.setUserProfilePic(profilePic);
        when(userService.getUser(userId)).thenReturn(user);

        doNothing().when(s3Service).deleteImageFromBucket(anyString(), anyString());
        doNothing().when(userService).saveUser(user);

        userAvatarService.deleteAvatar(userId);

        verify(s3Service, times(1)).deleteImageFromBucket(customAvatarsStorage, largeImageKey);
        verify(s3Service, times(1)).deleteImageFromBucket(customAvatarsStorage, smallImageKey);

        verify(userService, times(1)).saveUser(user);
        assertNull(user.getUserProfilePic());
    }
}