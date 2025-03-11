package school.faang.user_service.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.channel")
public class Channels {
    private String recommendationChannel;
    private String recommendationMentorshipOffered;
    private String achievementChannel;
    private String followerChannel;
    private String profileView;
    private String userBanChannel;
}
