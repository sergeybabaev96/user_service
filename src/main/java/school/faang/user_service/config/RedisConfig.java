package school.faang.user_service.config;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @NotNull
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @NotNull
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @NotNull
    @Value("${spring.data.redis.channel.skill-channel}")
    private String skillAcquiredTopic;

    @NotNull
    @Value("${spring.data.redis.channel.follower-topic}")
    private String followerTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    ChannelTopic skillChannel() {
        return new ChannelTopic(skillAcquiredTopic);
    }

    @Bean
    ChannelTopic followerChannel() {
        return new ChannelTopic(followerTopic);
    }

}
