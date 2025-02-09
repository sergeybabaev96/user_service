package school.faang.user_service.config.premium;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "premium.removal")
public class PremiumConfig {
    private String cron;
    private int batchSize;
    private int threadPoolSize;
}