package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${scheduler.thread-pool-size}")
    private int numThreads;

    @Value("${scheduler.event-batch-size}")
    private int batchSize;


    @Value("${scheduler.queue-capacity}")
    private int queueCapacity;

    @Value("${scheduler.keep-alive-time}")
    private long keepAliveTime;

    @Bean
    public ExecutorService threadPool() {

        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueCapacity);

        return new ThreadPoolExecutor(
                numThreads,
                numThreads,
                keepAliveTime,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy() );
    }
}
