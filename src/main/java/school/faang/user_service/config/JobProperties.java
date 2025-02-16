package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.support.CronExpression;

@Data
@Configuration
@ConfigurationProperties(prefix = "job")
public class JobProperties {
    private Integer batchSize;
    private Integer threadPoolSize;
    private CronExpression removePremiumCron;
}
