package school.faang.user_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import school.faang.user_service.properties.KafkaTopicsProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private final KafkaTopicsProperties topicProp;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(topicProp.getPostTopicName(), topicProp.getPostPartitionsCount(),
                topicProp.getPostReplicationsCount());
    }

    @Bean
    public NewTopic heatFeedCacheTopic() {
        return new NewTopic(topicProp.getHeatFeedCacheTopicName(), topicProp.getHeatFeedCachePartitionsCount(),
                topicProp.getHeatFeedCacheReplicationsCount());
    }
}
