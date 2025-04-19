package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Конфигурационный класс для настройки Redis в приложении.
 * Предоставляет бины для подключения к Redis, сериализации данных и обработки сообщений.
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Создает фабрику подключений Jedis к Redis.
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                properties.getHost(), properties.getPort());

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
                .readTimeout(Duration.ofMillis(properties.getReadTimeout()))
                .build();

        return new JedisConnectionFactory(config, clientConfig);
    }

    /**
     * Создает и настраивает RedisTemplate для работы с Redis.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);

        return template;
    }
}