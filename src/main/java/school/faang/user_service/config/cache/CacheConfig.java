package school.faang.user_service.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import school.faang.user_service.properties.RedisProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("promotionPlans",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisProperties.getTtlMinutes())));
        cacheConfigurations.put("promotionPlanByName",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisProperties.getTtlMinutes())));
        cacheConfigurations.put("promotionPlanByPrice",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisProperties.getTtlMinutes())));
        return  RedisCacheManager
                .builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}