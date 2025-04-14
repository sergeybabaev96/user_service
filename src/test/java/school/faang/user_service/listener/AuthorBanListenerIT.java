package school.faang.user_service.listener;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Testcontainers
public class AuthorBanListenerIT {

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Value("${spring.data.redis.channel.user-ban}")
    private String userBanTopic;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void testPositiveAuthorBanEventListener() throws InterruptedException {
        Long authorId = 1L;
        Country country = countryRepository.save(createCountry());
        User user = userRepository.save(createUser(authorId, country));

        redisTemplate.convertAndSend(userBanTopic, authorId);
        Thread.sleep(1000);

        User bannedUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("message"));

        assertEquals(user.getId(), bannedUser.getId());
        assertNotEquals(user.isBanned(), bannedUser.isBanned());
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Country createCountry() {
        return Country.builder()
                .title("Russia")
                .build();
    }

    private User createUser(Long id, Country country) {
        return User.builder()
                .id(id)
                .username("admin")
                .email("admin@faang.com")
                .password("admin")
                .country(country)
                .banned(false)
                .build();
    }
}
