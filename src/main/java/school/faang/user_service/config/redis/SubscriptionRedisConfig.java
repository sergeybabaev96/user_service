package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SubscriptionRedisConfig {

    private final SubscriptionRedisProperties subscriptionRedisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                subscriptionRedisProperties.getHost(),
                subscriptionRedisProperties.getPort()
        );

        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(subscriptionRedisProperties.getPool().getMaxActive());
        poolConfig.setMaxIdle(subscriptionRedisProperties.getPool().getMaxIdle());
        poolConfig.setMinIdle(subscriptionRedisProperties.getPool().getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(subscriptionRedisProperties.getPool().getMaxWait()));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(subscriptionRedisProperties.getTimeout()))
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(subscriptionRedisProperties.getAutoReconnect())
                        .build())
                .clientResources(ClientResources.builder()
                        .ioThreadPoolSize(subscriptionRedisProperties.getPool().getIoThreadPoolSize())
                        .computationThreadPoolSize(subscriptionRedisProperties.getPool().getComputationThreadPoolSize())
                        .build())
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RetryTemplate redisRetryTemplate() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(subscriptionRedisProperties.getRetry().getInitialDelay());
        backOffPolicy.setMaxInterval(subscriptionRedisProperties.getRetry().getMaxDelay());
        backOffPolicy.setMultiplier(subscriptionRedisProperties.getRetry().getMultiplier());

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
                subscriptionRedisProperties.getRetry().getMaxAttempts(),
                Collections.singletonMap(RedisConnectionFailureException.class, true)
        );

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> subscriptionRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }
}