package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.avatar.AvatarConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.s3.S3Service;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.avatar.AvatarValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
    @Mock
    private AvatarConfig avatarConfig;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserService userService;
    @Mock
    private AvatarValidator avatarValidator;
    @Mock
    private ImageResize imageResize;
    @Mock
    private MultipartFile avatarFile;
    @Mock
    private Resource avatarResource;

    @InjectMocks
    private AvatarService avatarService;

    private User user;
    private final long userId = 1L;
    private final String username = "testuser";
    private final String folderTemplate = "users/{username}/avatars";
    private final String smallFileKey = "small_key";
    private final String largeFileKey = "large_key";

    @BeforeEach
    public void setUp() throws IOException {
        user = User.builder().id(userId).username(username).build();
    }

    @DisplayName("Проверка успешного добавления аватара пользователя с созданием двух версий")
    @Test
    public void givenValidData_WhenAddUserAvatar_ThenSuccess() {
        Map<String, Integer> sizes = new HashMap<>();
        sizes.put("small", 100);
        sizes.put("large", 200);

        when(userService.getUserEntity(userId)).thenReturn(user);
        when(avatarConfig.getSizes()).thenReturn(sizes);

        MultipartFile smallAvatar = mock(MultipartFile.class);
        MultipartFile largeAvatar = mock(MultipartFile.class);

        when(imageResize.resizeImage(any(), eq(100))).thenReturn(smallAvatar);
        when(imageResize.resizeImage(any(), eq(200))).thenReturn(largeAvatar);

        when(avatarConfig.getFolderTemplate()).thenReturn(folderTemplate);

        when(s3Service.uploadFile(smallAvatar, "users/testuser/avatars/small")).thenReturn(smallFileKey);
        when(s3Service.uploadFile(largeAvatar, "users/testuser/avatars/large")).thenReturn(largeFileKey);

        avatarService.addUserAvatar(userId, avatarFile);

        verify(avatarValidator).checkAvatarSize(avatarFile);
        verify(userService).getUserEntity(userId);
        verify(imageResize).resizeImage(avatarFile, 100);
        verify(imageResize).resizeImage(avatarFile, 200);
        verify(s3Service).uploadFile(smallAvatar, "users/testuser/avatars/small");
        verify(s3Service).uploadFile(largeAvatar, "users/testuser/avatars/large");
    }

    @DisplayName("Проверка успешного удаления аватара пользователя с очисткой профиля")
    @Test
    void givenValidData_WhenDeleteUserAvatar_ThenSuccess() {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(largeFileKey);
        profilePic.setSmallFileId(smallFileKey);
        user.setUserProfilePic(profilePic);

        when(userService.getUserEntity(userId)).thenReturn(user);

        avatarService.deleteUserAvatar(userId);

        verify(s3Service).deleteFile(largeFileKey);
        verify(s3Service).deleteFile(smallFileKey);
        assertNull(user.getUserProfilePic());
    }

    @DisplayName("Проверка успешного получения аватара пользователя из хранилища")
    @Test
    void givenValidData_WhenGetUserAvatar_ThenSuccess() {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(largeFileKey);
        user.setUserProfilePic(profilePic);

        when(userService.getUserEntity(userId)).thenReturn(user);
        when(s3Service.downloadFile(largeFileKey)).thenReturn(avatarResource);

        Resource result = avatarService.getUserAvatar(userId);

        assertEquals(avatarResource, result);
        verify(userService).getUserEntity(userId);
        verify(s3Service).downloadFile(largeFileKey);
    }
}