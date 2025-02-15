package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.properties.AvatarProperties;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private AvatarFeignClient avatarFeignClient;

    @Mock
    private AvatarProperties avatarProperties;

    @Mock
    private ByteArrayResource defaultAvatar;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    @Test
    public void testSaveRandomAvatarsToS3() {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        when(avatarFeignClient.getAvatar(any(), anyInt())).thenReturn(image);

        Pair<String, String> avatars = avatarService.saveRandomAvatarsToS3(User.builder().build());

        assertNotNull(avatars);
    }

    @Test
    public void testSaveRandomAvatarsToS3WhenFeignClientNotAvailable() {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        when(avatarProperties.getBucket()).thenReturn("bucket");
        when(avatarProperties.getDefaultAvatar()).thenReturn("avatar");
        when(s3Service.downloadFile(anyString(), anyString())).thenReturn(new ByteArrayInputStream(image));
        when(avatarFeignClient.getAvatar(any(), anyInt())).thenThrow(new RuntimeException("Feign client error"));

        avatarService.init();
        Pair<String, String> avatars = avatarService.saveRandomAvatarsToS3(User.builder().build());

        assertNotNull(avatars);
    }
}