package school.faang.user_service.config.context;

import jakarta.annotation.PreDestroy;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.exception.AsyncExceptionHandler;

import java.util.concurrent.Executor;


@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private ThreadPoolTaskExecutor executor;

    @Override
    @Bean(name = "threadPoolOfScheduling")
    public Executor getAsyncExecutor() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
