package school.faang.user_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class S3Config {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Bean(name = "clientAmazonS3")
    public AmazonS3 amazons3() {
        log.info("Initializing S3 client for endpoint: {}", endpoint);

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 clientAmazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "us-east-1"))
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        log.info("S3 client initialized successfully for bucket: {}", bucketName);
        return clientAmazonS3;
    }
}