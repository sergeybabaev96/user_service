package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;

import java.io.InputStream;

public interface AvatarService {

    Pair<String, String> saveRandomAvatarsToS3(User user);

    void saveAvatars(long userId, MultipartFile file);

    InputStream getAvatarByUser(long userId, String size);

    InputStream getAvatarByKey(String key);

    void deleteAvatarByUser(long userId);

    void deleteAvatarByKey(String key);
}
