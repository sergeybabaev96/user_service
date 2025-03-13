package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.DeleteAvatarResponse;
import school.faang.user_service.dto.avatar.GetAvatarResponse;
import school.faang.user_service.dto.avatar.UploadAvatarResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.resource.ResourseService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.ResourseValidator;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AvatarServiceTest {

    private UserService userService;
    private S3Service s3Service;
    private ResourseService resourseService;
    private ResourseValidator resourseValidator;
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        s3Service = mock(S3Service.class);
        resourseService = mock(ResourseService.class);
        resourseValidator = mock(ResourseValidator.class);
        avatarService = new AvatarService(userService, s3Service, resourseService, resourseValidator);
    }

    @Test
    void uploadAvatarShouldUploadFilesAndSaveUserProfilePic() throws Exception {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        byte[] fileBytes = new byte[]{1, 2, 3};
        String format = "png";

        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getBytes()).thenReturn(fileBytes);
        when(resourseValidator.getFileExtension(file)).thenReturn(format);

        byte[] mediumImage = new byte[]{4, 5};
        byte[] smallImage = new byte[]{6, 7};
        when(resourseService.resize(fileBytes, 1080, format)).thenReturn(mediumImage);
        when(resourseService.resize(fileBytes, 170, format)).thenReturn(smallImage);

        User user = new User();
        when(userService.findUserById(userId)).thenReturn(user);

        UploadAvatarResponse response = avatarService.uploadAvatar(userId, file);

        assertNotNull(response);
        assertTrue(response.getFileId().contains("avatars/" + userId));

        verify(resourseValidator)
                .validate(file);
        verify(s3Service, times(3))
                .uploadFile(anyString(), any(ByteArrayInputStream.class), anyLong(), anyString());
        verify(userService)
                .findUserById(userId);
    }

    @Test
    void getAvatarShouldReturnPresignedUrls() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("file.png");
        pic.setMediumFileId("file_medium.png");
        pic.setSmallFileId("file_small.png");
        Long userId = 1L;
        User user = new User();
        user.setUserProfilePic(pic);

        when(userService.findUserById(userId)).thenReturn(user);
        when(s3Service.generatePresignedUrl("file.png")).thenReturn("http://s3/file.png");
        when(s3Service.generatePresignedUrl("file_medium.png")).thenReturn("http://s3/file_medium.png");
        when(s3Service.generatePresignedUrl("file_small.png")).thenReturn("http://s3/file_small.png");

        GetAvatarResponse response = avatarService.getAvatar(userId);

        assertEquals("http://s3/file.png", response.getAvatarUrl());
        assertEquals("http://s3/file_medium.png", response.getMediumAvatarUrl());
        assertEquals("http://s3/file_small.png", response.getSmallAvatarUrl());
    }

    @Test
    void deleteAvatarShouldDeleteFilesAndClearProfilePic() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("file.png");
        pic.setMediumFileId("file_medium.png");
        pic.setSmallFileId("file_small.png");
        Long userId = 1L;
        User user = new User();
        user.setUserProfilePic(pic);

        when(userService.findUserById(userId)).thenReturn(user);

        DeleteAvatarResponse response = avatarService.deleteAvatar(userId);

        assertTrue(response.isSuccess());
        assertEquals("Avatar deleted successfully", response.getMessage());
        verify(s3Service)
                .deleteFile("file.png");
        verify(s3Service)
                .deleteFile("file_medium.png");
        verify(s3Service)
                .deleteFile("file_small.png");
    }

    @Test
    void deleteAvatarShouldReturnFalseIfNoAvatar() {
        Long userId = 1L;
        User user = new User();
        user.setUserProfilePic(null);

        when(userService.findUserById(userId)).thenReturn(user);

        DeleteAvatarResponse response = avatarService.deleteAvatar(userId);

        assertFalse(response.isSuccess());
        assertEquals("User has no avatar", response.getMessage());
        verify(s3Service, never())
                .deleteFile(anyString());
    }
}