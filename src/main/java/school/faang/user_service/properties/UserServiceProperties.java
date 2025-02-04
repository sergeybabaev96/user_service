package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-service")
@Getter
@Setter
public class UserServiceProperties {
    private RecommendationRequestProperties recommendationRequest;

    @Getter
    @Setter
    public static class RecommendationRequestProperties {
        private int minMonth;
    }
}
