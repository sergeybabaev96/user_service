package school.faang.user_service.config.premium;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for the Premium Remover Executor.
 * This class configures a ThreadPoolTaskExecutor for handling premium removal tasks asynchronously.
 */
@Configuration
@ConfigurationProperties(prefix = "premium-remover")
@Data
public class PremiumRemoverConfig {
    private int coreSize;
    private int maxSize;
    private int queueCapacity;
    private String threadNamePrefix;

    @Bean
    public Executor premiumRemoverExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
