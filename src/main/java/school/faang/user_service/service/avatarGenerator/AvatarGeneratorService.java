package school.faang.user_service.service.avatarGenerator;

import org.springframework.core.io.buffer.DataBuffer;

public interface AvatarGeneratorService {
    DataBuffer getRandomAvatar();

    String getRandomAvatarContentType();
}
