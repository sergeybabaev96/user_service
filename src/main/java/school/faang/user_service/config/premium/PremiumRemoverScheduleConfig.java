package school.faang.user_service.config.premium;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "premium-remover.scheduled")
@Data
public class PremiumRemoverScheduleConfig {
    private String cron;
    private int batchSize;
}
