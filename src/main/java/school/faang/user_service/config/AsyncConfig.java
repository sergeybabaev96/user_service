package school.faang.user_service.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    @Value(value = "${executor.pool-number}")
    private int threadPoolNumber;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPoolNumber);
    }
}
