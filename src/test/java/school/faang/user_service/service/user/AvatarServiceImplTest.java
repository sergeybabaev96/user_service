package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.service.dicebear.DiceBearService;
import school.faang.user_service.service.s3.S3Service;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private AvatarProperties avatarProperties;

    @Mock
    private DiceBearService diceBearService;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    @Test
    public void testSaveRandomAvatarsToS3() {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        when(diceBearService.getRandomAvatar(any(), anyInt())).thenReturn(image);

        Pair<String, String> avatars = avatarService.saveRandomAvatarsToS3(User.builder().build());

        assertNotNull(avatars);
    }
}