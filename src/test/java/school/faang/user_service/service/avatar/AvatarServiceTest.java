package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.avatar.AvatarValidator;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private AvatarValidator avatarValidator;

    @Mock
    private UserService userService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AvatarService avatarService;

    private final int largerSize = 1080;
    private final int smallerSize = 170;
    private final long userId = 1L;
    User user = new User();
    UserProfilePic userProfilePic;
    String largeFileId = "large-avatar-key";
    String smallFileId = "small-avatar-key";

    @BeforeEach
    void setUp() {
        user.setId(userId);
        userProfilePic = new UserProfilePic();
        user.setUserProfilePic(userProfilePic);
        ReflectionTestUtils.setField(avatarService, "largerSize", 1080);
        ReflectionTestUtils.setField(avatarService, "smallerSize", 170);
    }

    @Test
    void testAddUserAvatarSuccessfully() {
        String folder = userId + "_user_avatars";

        when(userService.getUserFromDb(userId)).thenReturn(user);
        doNothing().when(avatarValidator).checkMaxFileSize(any(MultipartFile.class), anyLong());
        when(s3Service.uploadFile(multipartFile, folder, largerSize)).thenReturn(largeFileId);
        when(s3Service.uploadFile(multipartFile, folder, smallerSize)).thenReturn(smallFileId);
        System.out.println("Uploading with size: " + largerSize);
        System.out.println("Uploading with size: " + smallerSize);
        avatarService.addUserAvatar(userId, multipartFile);

        assertNotNull(user.getUserProfilePic());
        assertEquals(largeFileId, user.getUserProfilePic().getFileId());
        assertEquals(smallFileId, user.getUserProfilePic().getSmallFileId());
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserAvatarReturnInputStream() {
        String fileKey = "avatar-key";
        InputStream inputStreamMock = mock(InputStream.class);
        userProfilePic.setFileId(fileKey);

        when(userService.getUserFromDb(userId)).thenReturn(user);
        when(s3Service.downloadFile(fileKey)).thenReturn(inputStreamMock);

        InputStream result = avatarService.getUserAvatar(userId);

        assertNotNull(result);
        assertEquals(inputStreamMock, result);
        verify(s3Service).downloadFile(fileKey);
    }

    @Test
    void testRemoveUserAvatarSuccessfully() {
        userProfilePic.setFileId(largeFileId);
        userProfilePic.setSmallFileId(smallFileId);

        when(userService.getUserFromDb(userId)).thenReturn(user);
        avatarService.removeUserAvatar(userId);

        verify(s3Service).deleteFile(largeFileId);
        verify(s3Service).deleteFile(smallFileId);
        assertNull(user.getUserProfilePic());
        verify(userRepository).save(user);
    }

}
