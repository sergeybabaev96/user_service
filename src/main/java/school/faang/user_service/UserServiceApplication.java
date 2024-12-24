package school.faang.user_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import school.faang.user_service.config.redis.RedisProperties;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
@OpenAPIDefinition(
        info = @Info(
                title = "User Service",
                version = "1.0.0")
)
@EnableAsync
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties(RedisProperties.class)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}