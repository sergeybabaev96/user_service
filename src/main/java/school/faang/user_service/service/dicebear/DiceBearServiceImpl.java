package school.faang.user_service.service.dicebear;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.exception.DownloadS3Exception;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiceBearServiceImpl implements DiceBearService {

    private final AvatarFeignClient avatarFeignClient;
    private final S3Service s3Service;
    private final AvatarProperties avatarProperties;

    @Getter
    private byte[] defaultAvatar;

    @PostConstruct
    public void init() {
        try (InputStream object = s3Service.downloadFile(avatarProperties.getBucket(),
                avatarProperties.getDefaultAvatar())) {
            defaultAvatar = object.readAllBytes();
        } catch (IOException e) {
            log.error("Download default avatar failed.", e);
            throw new DownloadS3Exception("Download default avatar failed");
        }
    }

    @Override
    public byte[] getRandomAvatar(String seed, int size) {
        try {
            return avatarFeignClient.getAvatar(seed, size);
        } catch (Exception e) {
            log.warn("Failed to get avatar from from DiceBear. Use default avatar.");
            return defaultAvatar;
        }
    }
}
