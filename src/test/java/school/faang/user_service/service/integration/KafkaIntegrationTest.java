package school.faang.user_service.service.integration;

import com.redis.testcontainers.RedisContainer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.dto.kafka.FollowUserEventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Import(TestKafkaConfig.class)
@SpringBootTest
public class KafkaIntegrationTest {
    public static final String TOPIC = "user-follows-user";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private TestMessageConsumer testMessageConsumer;

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Container
    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @BeforeAll
    static void createKafkaTopic() {
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
        try {
            org.testcontainers.containers.Container.ExecResult result = kafkaContainer.execInContainer(
                    "/bin/kafka-topics.sh",
                    "--create",
                    "--bootstrap-server",
                    "localhost:9092",
                    "--replication-factor",
                    "1",
                    "--partitions",
                    "1",
                    "--topic",
                    TOPIC
            );
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Kafka topic: " + TOPIC, e);
        }
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendMessageToKafkaTopic() throws Exception {
        long followerId = 1L;
        long followeeId = 9L;
        FollowUserEventDto eventDto = new FollowUserEventDto(followerId, followeeId, LocalDateTime.now());

        testMessageConsumer.reset();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                        .param("followerId", String.valueOf(followerId))
                        .param("followeeId", String.valueOf(followeeId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    FollowUserEventDto receivedEvent = testMessageConsumer.getPayload();
                    assertThat(receivedEvent).isNotNull();
                    assertThat(receivedEvent.followeeId()).isEqualTo(followeeId);
                    assertThat(receivedEvent.followerId()).isEqualTo(followerId);
                    assertThat(receivedEvent.followedAt()).isAfter(eventDto.followedAt());
                });
    }
}
