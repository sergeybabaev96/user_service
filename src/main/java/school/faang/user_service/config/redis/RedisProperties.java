package school.faang.user_service.config.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс для хранения настроек подключения к Redis.
 * <p>
 * Свойства настраиваются через application.yml с префиксом spring.data.redis
 * </p>
 */
@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private final int port;
    private final String host;
    private final int connectTimeout;
    private final int readTimeout;
}