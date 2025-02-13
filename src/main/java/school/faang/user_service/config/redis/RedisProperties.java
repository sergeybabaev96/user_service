package school.faang.user_service.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RedisProperties.class)
@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(String host, int port, Channel channel) {

    public record Channel(String mentorshipChannel,
                          String subscriptionChannel,
                          String recommendationChannel,
                          String userBanChannel,
                          String mentorshipRequest,
                          String recommendationRequestChannel) {
    }
}
