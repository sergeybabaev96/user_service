package school.faang.user_service.config.dicebear;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "dicebear")
@Configuration
public class DiceBearApiConfig {
    private String apiUrl;
    private long connectionTimeoutSeconds;
    private long readTimeoutSeconds;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.of(connectionTimeoutSeconds, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(readTimeoutSeconds, ChronoUnit.SECONDS))
                .build();
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public DicebearStyleGenerator dicebearStyleGenerator(SecureRandom random) {
        return new DicebearStyleGenerator(random);
    }
}