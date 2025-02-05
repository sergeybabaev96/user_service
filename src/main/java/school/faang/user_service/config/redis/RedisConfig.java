package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    private final Jackson2JsonRedisSerializer<Object> defaultSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.ban-channel.name}")
    private String userBanChannelName;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        RedisConnectionFactory connectionFactory = redisConnectionFactory();

        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(defaultSerializer);

        return template;
    }

    @Bean
    public MessageListenerAdapter userBanMessageListener(UserBanSubscriber subscriber) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(subscriber, "handleMessage");
        listenerAdapter.setSerializer(defaultSerializer);
        return listenerAdapter;
    }

    @Bean
    public ChannelTopic userBanChannel() {
        return new ChannelTopic(userBanChannelName);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(MessageListenerAdapter userBanMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        RedisConnectionFactory connectionFactory = redisConnectionFactory();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(userBanMessageListener, userBanChannel());

        return container;
    }
}
