package school.faang.user_service.service.user;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DownloadMinioException;
import school.faang.user_service.properties.AvatarProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private final MinioService minioService;
    private final AvatarFeignClient avatarFeignClient;
    private final AvatarProperties avatarProperties;

    @Getter
    private ByteArrayResource defaultAvatar;

    @PostConstruct
    public void init() {
        try (InputStream object = minioService.downloadFile(avatarProperties.getBucket(),
                avatarProperties.getDefaultAvatar())) {
            defaultAvatar = new ByteArrayResource(object.readAllBytes());
        } catch (IOException e) {
            log.error("Download default avatar failed.", e);
            throw new DownloadMinioException("");
        }
    }

    public Pair<String, String> saveAvatarsToMinio(User user) {
        String randomId = UUID.randomUUID().toString();
        ByteArrayResource avatar = getAvatarFromDiceBear(randomId, avatarProperties.getSize());
        ByteArrayResource smallAvatar = getAvatarFromDiceBear(randomId, avatarProperties.getSmallSize());

        String avatarId = minioService.uploadToMinio(avatar, avatarProperties.getBucket());
        String avatarSmallId = minioService.uploadToMinio(smallAvatar, avatarProperties.getBucket());

        return Pair.of(avatarId, avatarSmallId);
    }

    private ByteArrayResource getAvatarFromDiceBear(String seed, int size) {
        try {
            return avatarFeignClient.getAvatar(seed, size);
        } catch (Exception e) {
            log.warn("Failed to get avatar from from DiceBear. Use default avatar.");
            return defaultAvatar;
        }
    }

}
