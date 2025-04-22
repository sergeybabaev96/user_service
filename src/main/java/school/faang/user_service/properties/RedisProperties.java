package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.data.redis")
@Component
@Getter
@Setter
public class RedisProperties {
    private int port;
    private String host;
    private Map<String, String> topics;

    public String getTopic(EventType eventType){
        return topics.get(eventType.getKey());
    }
}
