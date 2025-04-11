package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
@Profile("test")
public class TestDicebearWebClientConfiguration {

    @Bean("dicebearWebClient")
    @Primary
    public WebClient getWebClient(@Autowired DicebearConfig config) {
        return WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }
}