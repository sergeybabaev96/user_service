package school.faang.user_service.config.aws;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "minio.s3")
@Configuration
public class AwsMinioApiConfig {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;

    private S3Presigner s3Presigner;

    @Bean
    public S3Client minioS3Client() {
//        return AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(
//                        new AwsClientBuilder.EndpointConfiguration(endpoint, region))
//                .withCredentials(new AWSStaticCredentialsProvider(
//                        new BasicAWSCredentials(accessKey, secretKey)))
//                .enablePathStyleAccess()
//                .build();
        return S3Client.builder()
                // Устанавливаем кастомный endpoint для MinIO
                .endpointOverride(URI.create(endpoint))
                // Задаём регион (например, "us-east-1")
                .region(Region.of(region))
                // Устанавливаем учётные данные
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                // Включаем режим path-style для совместимости с MinIO
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .build();
        return s3Presigner;
    }

    @PreDestroy
    public void shutdown() {
        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }
}
