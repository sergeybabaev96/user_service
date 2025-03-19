package school.faang.user_service.config.webclient;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static final int REQUEST_TIMEOUT_MS = 1000;
    private static final int RESPONSE_TIMEOUT_MS = 1000;

    @Value("${dicebear.initials-url}")
    private String dicebearUrl;

    @Bean
    public WebClient dicebearWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, REQUEST_TIMEOUT_MS)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS));

        return WebClient.builder()
                .baseUrl(dicebearUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
