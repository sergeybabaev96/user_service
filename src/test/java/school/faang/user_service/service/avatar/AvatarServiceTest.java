package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UploadAvatarRequest;
import school.faang.user_service.dto.avatar.UploadAvatarResponse;
import school.faang.user_service.dto.avatar.GetAvatarResponse;
import school.faang.user_service.dto.avatar.DeleteAvatarResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private AvatarService avatarService;

    private User testUser;
    private String testFileId;
    private String testMediumFileId;
    private String testSmallFileId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserProfilePic(new UserProfilePic());

        testFileId = String.format("avatars/%d/%s.png", testUser.getId(), UUID.randomUUID());
        testMediumFileId = String.format("avatars/%d/%s_medium.png", testUser.getId(), UUID.randomUUID());
        testSmallFileId = String.format("avatars/%d/%s_small.png", testUser.getId(), UUID.randomUUID());
    }

    @Disabled
    @Test
    void uploadAvatar_Success() throws IOException {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(4L * 1024 * 1024);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3, 4});

        doNothing().when(s3Client).putObject(any(PutObjectRequest.class)); // -

        UploadAvatarRequest request = new UploadAvatarRequest(file);
        UploadAvatarResponse response = avatarService.uploadAvatar(testUser.getId(), request); // -

        assertNotNull(response);
        assertTrue(response.getFileId().contains("avatars/"));
        assertTrue(response.getMediumFileId().contains("avatars/"));
        assertTrue(response.getSmallFileId().contains("avatars/"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(s3Client, times(3)).putObject(any(PutObjectRequest.class));
    }

    @Disabled
    @Test
    void uploadAvatar_FailsWhenFileTooLarge() {
        when(file.getSize()).thenReturn(6L * 1024 * 1024);

        UploadAvatarRequest request = new UploadAvatarRequest(file);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                avatarService.uploadAvatar(testUser.getId(), request)
        );

        assertEquals("File size exceeds the maximum limit of 5MB!", exception.getMessage()); // -
    }

    @Test
    void getAvatar_Success() throws MalformedURLException {
        testUser.getUserProfilePic().setFileId(testFileId);
        testUser.getUserProfilePic().setMediumFileId(testMediumFileId);
        testUser.getUserProfilePic().setSmallFileId(testSmallFileId);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(new URL("http://example.com/avatar.png"));

        GetAvatarResponse response = avatarService.getAvatar(testUser.getId());

        assertNotNull(response);
        assertTrue(response.getAvatarUrl().contains("http://example.com"));
        assertTrue(response.getMediumAvatarUrl().contains("http://example.com"));
        assertTrue(response.getSmallAvatarUrl().contains("http://example.com"));
    }

    @Test
    void getAvatar_FailsWhenUserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                avatarService.getAvatar(testUser.getId())
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getAvatar_FailsWhenNoAvatar() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                avatarService.getAvatar(testUser.getId())
        );

        assertEquals("User does not have an avatar", exception.getMessage());
    }

    @Disabled
    @Test
    void deleteAvatar_Success() {
        testUser.getUserProfilePic().setFileId(testFileId);
        testUser.getUserProfilePic().setMediumFileId(testMediumFileId);
        testUser.getUserProfilePic().setSmallFileId(testSmallFileId);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        doNothing().when(s3Client).deleteObject(anyString(), anyString());

        DeleteAvatarResponse response = avatarService.deleteAvatar(testUser.getId()); // -

        assertTrue(response.isSuccess());
        assertEquals("Avatar deleted successfully", response.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
        verify(s3Client, times(3)).deleteObject(anyString(), anyString());
    }

    @Test
    void deleteAvatar_FailsWhenUserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                avatarService.deleteAvatar(testUser.getId())
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteAvatar_FailsWhenNoAvatar() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        DeleteAvatarResponse response = avatarService.deleteAvatar(testUser.getId());

        assertFalse(response.isSuccess());
        assertEquals("User has no avatar", response.getMessage());
    }
}