package school.faang.user_service.config.goal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "goal.invitation")
public class GoalInvitationConfig {

    private int maxActiveGoals;
}
