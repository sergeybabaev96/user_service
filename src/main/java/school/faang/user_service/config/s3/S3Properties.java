package school.faang.user_service.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String endpoint,
        String accessKey,
        String secretKey,
        boolean isMocked,
        String bucketName
        ) {
}
