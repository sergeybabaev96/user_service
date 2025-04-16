package integration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTestTopicsConfig {

    @Bean
    public NewTopic premiumRequestTopic() {
        return TopicBuilder.name("premium-payment-request-topic")
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic premiumResponseTopic() {
        return TopicBuilder.name("premium-payment-response-topic")
                .partitions(1).replicas(1).build();
    }
}
