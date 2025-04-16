package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.properties.DicebearProperties;
import school.faang.user_service.util.DicebearStyleGenerator;

import java.security.SecureRandom;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class DiceBearApiConfig {

    private final DicebearProperties dicebearProperties;

    @Bean
    public RestTemplate diceBearRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(dicebearProperties.getConnectionTimeoutSeconds()))
                .setReadTimeout(Duration.ofSeconds(dicebearProperties.getReadTimeoutSeconds()))
                .build();
    }

    @Bean
    public SecureRandom secureRandom() { return new SecureRandom(); }

    @Bean
    public DicebearStyleGenerator dicebearStyleGenerator(SecureRandom random) {
        return new DicebearStyleGenerator(random);
    }
}
