package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.config.dicebear.AvatarType;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private DiceBearService diceBearService;
    @InjectMocks
    private UserAvatarService userAvatarService;
    private User user;
    private String randomFileName;

    @BeforeEach
    void setUp() {
        user = new User();
        randomFileName = "e9993c5c-8020-40d0-a965-f4c44485b3e5.jpg";
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

        assertEquals(actualUrl, expectedUrl);
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
}