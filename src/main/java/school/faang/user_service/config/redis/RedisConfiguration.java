package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import school.faang.user_service.listener.RedisUserBanTopicListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisConfig redisConfig;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisConfig.getHost(), redisConfig.getPort());
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisUserBanTopicListener userBanMessageListener,
            @Qualifier("user-ban") ChannelTopic userBanTopic) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(userBanMessageListener, userBanTopic);

        return container;
    }

    @Bean
    @Qualifier("user-ban")
    ChannelTopic userBanTopic() {
        return new ChannelTopic(redisConfig.getChannels().get("user_ban").name());
    }
}
