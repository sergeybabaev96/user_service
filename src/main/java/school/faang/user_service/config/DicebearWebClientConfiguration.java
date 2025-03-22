package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class DicebearWebClientConfiguration {

    private final DicebearConfig dicebearConfig;

    @Bean("dicebearWebClient")
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(dicebearConfig.getBaseUrl())
                .build();
    }
}
