package school.faang.user_service.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring.data.redis.channel")
@Validated
@Data
public class RedisChannelProperties {

    @NotBlank
    private String followerTopic;

    @NotBlank
    private String skillChannel;
}
