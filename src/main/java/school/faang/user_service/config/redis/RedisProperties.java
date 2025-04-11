package school.faang.user_service.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс для хранения настроек подключения к Redis.
 * <p>
 * Свойства настраиваются через application.yml с префиксом spring.data.redis
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
    private int connectTimeout;
    private int readTimeout;
}