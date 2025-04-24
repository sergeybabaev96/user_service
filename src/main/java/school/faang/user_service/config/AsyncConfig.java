package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean(name = "taskService")
    public Executor eventDeletionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(7);
        executor.setThreadNamePrefix("Async");
        executor.initialize();
        return executor;
    }
}
