package school.faang.user_service.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.config.s3.S3Properties;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarService {

    private final AmazonS3 amazonS3;
    private final UserService userService;
    private final WebClient webClient;
    private final S3Properties s3Properties;

    public String generateAndUploadAvatar(UserDto userDto) {
        User user = userService.getUserById(userDto.id());

        byte[] imageBytes = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("seed", user.getUsername())
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (imageBytes == null) {
            throw new RuntimeException("Failed to download avatar");
        }

        String key = "avatars/" + UUID.randomUUID() + ".png";
        uploadToS3(key, imageBytes);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(key);
        user.setUserProfilePic(userProfilePic);

        return amazonS3.getUrl(s3Properties.bucketName(), key).toString();
    }

    private void uploadToS3(String key, byte[] imageBytes) {
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");

        try {
            amazonS3.putObject(s3Properties.bucketName(), key, inputStream, metadata);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
        }
    }
}
