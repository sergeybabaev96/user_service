package school.faang.user_service.config.avatar;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Конфигурация параметров аватаров пользователей.
 * */
@Configuration
@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "avatar")
public class AvatarConfig {
    private long maxSizeBytes;

    @NotNull(message = "folderTemplate can not be null")
    private String folderTemplate;

    @NotNull(message = "sizes can not be null")
    private Map<String, Integer> sizes;
}
