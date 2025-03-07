package school.faang.user_service.service.integration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import school.faang.user_service.dto.kafka.FollowUserEventDto;
import school.faang.user_service.service.kafka.KafkaProducer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestConfiguration
public class TestKafkaConfig {
    private final TestMessageConsumer testMessageConsumer;

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    public TestKafkaConfig(@Lazy TestMessageConsumer testMessageConsumer) {
        this.testMessageConsumer = testMessageConsumer;
    }

    @Primary
    @Bean
    public KafkaProducer testKafkaProducer() {
        return new TestKafkaProducerImpl(testMessageConsumer);
    }

    static class TestKafkaProducerImpl extends KafkaProducer {
        private final TestMessageConsumer testMessageConsumer;

        public TestKafkaProducerImpl(TestMessageConsumer testMessageConsumer) {
            super(null);
            this.testMessageConsumer = testMessageConsumer;
        }

        @Override
        public void sendFollowUserEvent(Long followerId, Long followeeId) {
            FollowUserEventDto dto = new FollowUserEventDto(followerId, followeeId, LocalDateTime.now());
            testMessageConsumer.receive(dto);
        }
    }

    @Bean(name = "testConsumer")
    public ConsumerFactory<String, Object> consumerFactory() {
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(Object.class);
        jsonDeserializer.ignoreTypeHeaders();
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
