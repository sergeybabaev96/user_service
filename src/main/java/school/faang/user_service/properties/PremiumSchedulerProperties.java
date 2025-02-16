package school.faang.user_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "premium.scheduler")
@Data
public class PremiumSchedulerProperties {

    private int batch;
}