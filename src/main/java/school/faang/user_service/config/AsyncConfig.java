package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Value(value = "${executor.pool-number}")
    private int threadPoolNumber;

    @Bean(destroyMethod = "shutdown", name = "deletePremiumExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(threadPoolNumber);
        executor.setMaxPoolSize(threadPoolNumber * 2);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("deletePremiumExecutor-thread-");
        executor.initialize();
        return executor;
    }
}
