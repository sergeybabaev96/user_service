package school.faang.user_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "avatar")
@Data
public class AvatarProperties {

    private String defaultAvatar;

    private String bucket;

    private int size;

    private int smallSize;

}