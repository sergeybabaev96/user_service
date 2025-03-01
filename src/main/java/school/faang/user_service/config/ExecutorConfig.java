package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

    @Value("${scheduler.thread-pool-size}")
    private int numThreads;

    @Value("${scheduler.event-batch-size}")
    private int batchSize;

    @Value("${scheduler.keep-alive-time}")
    private int keepAliveTime;

    @Bean(destroyMethod = "shutdown", name = "deletePastEvents")
    public ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(numThreads);
        executor.setMaxPoolSize(numThreads * 2);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix("deletePastEvents-thread-");
        executor.initialize();
        return executor;
    }
}