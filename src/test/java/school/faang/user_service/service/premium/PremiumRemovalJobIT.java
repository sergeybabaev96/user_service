package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback
@Transactional
@SpringBootTest
@Tag("integration") // Это Альфир так сказал сделать. Стёпа.
class PremiumRemovalJobIT {

    @Autowired
    private PremiumRepository premiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PremiumRemovalJob premiumRemovalJob;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setTitle("USA");
        country = countryRepository.save(country);

        User testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("hashedPassword")
                .active(true)
                .country(country)
                .build();
        testUser = userRepository.save(testUser);

        Premium premium = Premium.builder()
                .user(testUser)
                .startDate(LocalDateTime.now().minusMonths(2))
                .endDate(LocalDateTime.now().minusDays(10))
                .build();
        premiumRepository.save(premium);
    }

    @Test
    void shouldRemoveExpiredPremiums() {
        long countBefore = premiumRepository.count();
        assertThat(countBefore).isGreaterThan(0);

        premiumRemovalJob.execute(null);

        long countAfter = premiumRepository.count();
        assertThat(countAfter).isZero();
    }
}