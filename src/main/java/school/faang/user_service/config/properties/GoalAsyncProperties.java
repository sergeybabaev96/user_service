package school.faang.user_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "goal-event.async")
public class GoalAsyncProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}