package school.faang.user_service.config.redis;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
public class SubscriptionRedisProperties {

    @NotBlank(message = "Redis host must not be blank")
    private String host;

    @NotNull(message = "Redis port must not be null")
    @Min(value = 1, message = "Redis port must be at least 1")
    @Max(value = 65535, message = "Redis port must not exceed 65535")
    private Integer port;

    @NotNull
    @Min(100)
    private Long timeout;

    @NotNull
    private Boolean autoReconnect;

    private Channel channel;

    @Getter
    @Setter
    public static class Channel {

        @NotBlank(message = "Follower channel must not be blank")
        private String follower;

        @NotBlank(message = "Unfollower channel must not be blank")
        private String unfollower;
    }

    private Pool pool;

    @Getter
    @Setter
    public static class Pool {

        @NotNull
        @Min(1)
        private Integer maxActive;

        @NotNull
        @Min(1)
        private Integer maxIdle;

        @NotNull
        @Min(0)
        private Integer minIdle;

        @NotNull
        @Min(0)
        private Long maxWait;

        @NotNull
        private Integer ioThreadPoolSize;

        @NotNull
        private Integer computationThreadPoolSize;
    }

    private Retry retry;

    @Getter
    @Setter
    public static class Retry {

        @NotNull
        @Min(0)
        private Integer maxAttempts;

        @NotNull
        @Min(0)
        private Long initialDelay;

        @NotNull
        @Min(0)
        private Long maxDelay;

        @NotNull
        private Double multiplier;
    }
}