package school.faang.user_service.service.avatarGenerator;

import java.io.IOException;
import java.io.InputStream;

public interface AvatarGeneratorService {
    byte[] getRandomAvatar() throws IOException;
}
