package school.faang.user_service.config;

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
@EnableConfigurationProperties(DicebearProperties.class)
public class DiceBearApiConfig {
    @Bean
    public RestTemplate diceBearRestTemplate(RestTemplateBuilder builder, DicebearProperties properties) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(properties.getConnectionTimeoutSeconds()))
                .setReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()))
                .build();
    }


    @Bean
    public SecureRandom secureRandom() { return new SecureRandom(); }

    @Bean
    public DicebearStyleGenerator dicebearStyleGenerator(SecureRandom random) {
        return new DicebearStyleGenerator(random);
    }
}
