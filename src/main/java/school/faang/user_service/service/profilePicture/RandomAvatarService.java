package school.faang.user_service.service.profilePicture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RandomAvatarService {
    private final DiceBearClient diceBearClient;
    private final AvatarService avatarService;
    private final Random random = new Random();
    private static final List<String> AVATAR_STYLES = List.of(
            "adventurer", "bottts", "croodles", "micah", "personas", "lorelei"
    );


    public String generateAndUploadAvatar(String seed) throws IOException {
        String randomStyle = AVATAR_STYLES.get(random.nextInt(AVATAR_STYLES.size()));

        byte[] avatarBytes = diceBearClient.getAvatar(randomStyle, seed, "png");

        if (avatarBytes == null || avatarBytes.length == 0) {
            throw new IOException("Не удалось сгенерировать аватар. Нет данных от DiceBear.");
        }

        ByteArrayInputStream avatarStream = new ByteArrayInputStream(avatarBytes);

        MultipartFile multipartFile = new CustomMultipartFile(
                avatarBytes,
                "avatar.png",
                "avatar.png",
                "image/png"
        );

        return avatarService.uploadAvatar(multipartFile);
    }
}
