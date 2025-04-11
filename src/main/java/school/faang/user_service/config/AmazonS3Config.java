package school.faang.user_service.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "services.s3")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class AmazonS3Config {
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String signingRegion;
}
