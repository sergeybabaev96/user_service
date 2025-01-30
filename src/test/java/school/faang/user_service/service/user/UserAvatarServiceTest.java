package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
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

    @InjectMocks
    private UserAvatarService userAvatarService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testGetUserAvatarCorrect() {
        UserProfilePic profilePic = new UserProfilePic();
        String fileId = "test-file-id";
        profilePic.setFileId(fileId);
        user.setUserProfilePic(profilePic);
        String expectedUrl = "test-url";

        when(s3Service.getUnexpiredUrl(any())).thenReturn(expectedUrl);

        String actualUrl = userAvatarService.getUserAvatar(user);

        assertEquals(expectedUrl, actualUrl);
        verify(s3Service, times(1)).getUnexpiredUrl(any());
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