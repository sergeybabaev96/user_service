package school.faang.user_service.config.kafka.topic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "user-profile-viewed")
public class UserProfileViewedTopicConfig {
    private String topicName;
    private int partitions;
    private short replicas;
}
