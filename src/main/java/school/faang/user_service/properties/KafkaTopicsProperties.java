package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.kafka.topics")
public class KafkaTopicsProperties {

    private String postTopicName;
    private int postPartitionsCount;
    private short postReplicationsCount;

    private String heatFeedCacheTopicName;
    private int heatFeedCachePartitionsCount;
    private short heatFeedCacheReplicationsCount;

}
