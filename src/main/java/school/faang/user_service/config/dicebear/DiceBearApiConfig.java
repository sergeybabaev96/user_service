package school.faang.user_service.config.dicebear;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;


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

    @Bean(name = "randomStyleGenerator")
    public Random random() {
        return new Random();
    }

    @Bean
    public DicebearStyleGenerator dicebearStyleGenerator(@Qualifier("randomStyleGenerator") Random random) {
        return new DicebearStyleGenerator(random);
    }
}
