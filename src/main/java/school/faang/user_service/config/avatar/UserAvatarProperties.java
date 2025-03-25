package school.faang.user_service.config.avatar;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "avatar")
public class UserAvatarProperties {
    private String bucketName;
    private Integer sizeMB;
    private Integer bigSide;
    private Integer smallSide;
}
