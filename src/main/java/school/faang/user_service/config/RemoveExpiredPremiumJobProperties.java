package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "remove-premium-job")
public class RemoveExpiredPremiumJobProperties {
    private Integer batchSize;
    private Integer threadPoolSize;
}
