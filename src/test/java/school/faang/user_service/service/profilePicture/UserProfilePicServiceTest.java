package school.faang.user_service.service.profilePicture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.BusinessException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfilePicServiceTest {

    @Mock
    private AvatarService avatarService;

    @Mock
    private RandomAvatarService randomAvatarService;

    @InjectMocks
    private UserProfilePicService userProfilePicService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProfilePicture_withUploadedProfilePic_returnsUserProfilePic() throws IOException {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("TestUser");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        userCreateDto.setProfilePic(mockFile);

        when(avatarService.uploadAvatar(mockFile)).thenReturn("profilePicUrl");
        when(avatarService.uploadThumbnailAvatar(mockFile)).thenReturn("thumbnailUrl");

        UserProfilePic result = userProfilePicService.createProfilePicture(userCreateDto);

        assertNotNull(result);
        assertEquals("profilePicUrl", result.getFileId());
        assertEquals("thumbnailUrl", result.getSmallFileId());
        verify(avatarService).uploadAvatar(mockFile);
        verify(avatarService).uploadThumbnailAvatar(mockFile);
    }

    @Test
    void createProfilePicture_withIOExceptionOnUpload_throwsBusinessException() throws IOException {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("TestUser");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        userCreateDto.setProfilePic(mockFile);

        when(avatarService.uploadAvatar(mockFile)).thenThrow(new IOException("Upload failed"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userProfilePicService.createProfilePicture(userCreateDto));

        assertEquals("Ошибка при загрузке аватарки", exception.getMessage());
        verify(avatarService).uploadAvatar(mockFile);
    }

    @Test
    void createProfilePicture_withGeneratedAvatar_returnsUserProfilePic() throws IOException {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("TestUser");

        when(randomAvatarService.generateAndUploadAvatar("TestUser")).thenReturn("generatedProfilePicUrl");

        UserProfilePic result = userProfilePicService.createProfilePicture(userCreateDto);

        assertNotNull(result);
        assertEquals("generatedProfilePicUrl", result.getFileId());
        assertNull(result.getSmallFileId());
        verify(randomAvatarService).generateAndUploadAvatar("TestUser");
    }

    @Test
    void createProfilePicture_withIOExceptionOnGeneratedAvatar_throwsBusinessException() throws IOException {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("TestUser");

        when(randomAvatarService.generateAndUploadAvatar("TestUser")).thenThrow(new IOException("Generation failed"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userProfilePicService.createProfilePicture(userCreateDto));

        assertEquals("Ошибка при генерации и загрузке аватарки", exception.getMessage());
        verify(randomAvatarService).generateAndUploadAvatar("TestUser");
    }
}
