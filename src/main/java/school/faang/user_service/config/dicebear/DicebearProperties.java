package school.faang.user_service.config.dicebear;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dicebear")
public class DicebearProperties {
    private String apiUrl;
    private long connectionTimeoutSeconds;
    private long readTimeoutSeconds;
}

