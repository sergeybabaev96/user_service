package school.faang.user_service.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${thread_pool.max_threads}")
    private final int ThreadsNumber;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(ThreadsNumber);
    }
}