package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
public class UserBanSubscriberIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Value("${spring.data.redis.channels.ban-channel.name}")
    private String banChannelName;

    private User testUser;

    @BeforeEach
    @Transactional
    void setup() {
        Country testCountry = countryRepository.findById(1L).get();
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setEmail("testEmail@gmail.com");
        testUser.setCountry(testCountry);
        testUser.setBanned(false);
        userRepository.save(testUser);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    public void whenMessagePublished_thenUserBannedFieldUpdated() throws InterruptedException {
        redisTemplate.convertAndSend(banChannelName, Arrays.asList(testUser.getId()));
        TimeUnit.SECONDS.sleep(1);

        Optional<User> updatedUserOpt = userRepository.findById(testUser.getId());
        assertThat(updatedUserOpt).isPresent();
        User updatedUser = updatedUserOpt.get();
        assertThat(updatedUser.getBanned()).isTrue();
    }
}
