package school.faang.user_service.config.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "event.start-notification")
public class EventStartEventNotificationConfig {
    private List<Interval> intervals;

    @Data
    public static class Interval {
        private int time;
        private String message;
    }
}
