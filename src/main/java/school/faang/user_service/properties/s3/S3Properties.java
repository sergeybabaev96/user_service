package school.faang.user_service.properties.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
@Data
public class S3Properties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

}