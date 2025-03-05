package school.faang.user_service.config.context;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.properties.UserServiceProperties;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final UserServiceProperties userServiceProperties;

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        userServiceProperties.getS3().getEndpoint(), Regions.EU_SOUTH_1.name()))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(userServiceProperties.getS3().getAccessKey(), userServiceProperties.getS3().getSecretKey()))
                )
                .build();
    }
}
