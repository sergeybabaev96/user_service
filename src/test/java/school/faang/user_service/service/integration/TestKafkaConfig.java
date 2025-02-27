package school.faang.user_service.service.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import school.faang.user_service.dto.kafka.FollowUserEventDto;
import school.faang.user_service.service.kafka.KafkaProducer;

import java.time.LocalDateTime;

@TestConfiguration
@Profile("test-mock-consumer")
public class TestKafkaConfig {
    private final TestMessageConsumer testMessageConsumer;

    public TestKafkaConfig(TestMessageConsumer testMessageConsumer) {
        this.testMessageConsumer = testMessageConsumer;
    }

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
}
