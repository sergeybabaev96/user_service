package school.faang.user_service.service;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.avatar.UserAvatarProperties;

@Configuration
public class TestUserAvatarProperties {

    @Bean
    public static UserAvatarProperties createTestProperties() {
        UserAvatarProperties properties = new UserAvatarProperties();
        properties.setBucketName("avatars");
        properties.setSizeMB(5);
        properties.setBigSide(1080);
        properties.setSmallSide(170);
        return properties;
    }
}
