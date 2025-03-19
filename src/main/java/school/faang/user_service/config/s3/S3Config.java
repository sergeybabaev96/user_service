package school.faang.user_service.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class S3Config {

    private static final int REQUEST_TIMEOUT = 1000;
    private static final int RESPONSE_TIMEOUT = 1000;

    private final S3Properties s3Properties;

    @Value("${dicebear.initials-url}")
    private String dicebearUrl;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Properties.endpoint(), "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(s3Properties.accessKey(), s3Properties.secretKey())))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, REQUEST_TIMEOUT)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT));

        return WebClient.builder()
                .baseUrl(dicebearUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
