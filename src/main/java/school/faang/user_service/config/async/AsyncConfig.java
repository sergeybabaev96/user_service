package school.faang.user_service.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean
    public ExecutorService cachedExecutorService() {
        return Executors.newFixedThreadPool(5);
    }
}
