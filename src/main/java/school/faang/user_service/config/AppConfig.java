package school.faang.user_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppConfig {
    @Value("${app.config.min_skill_offers:3}")
    private int minSkillOffers;

    @Value("${app.config.active_transaction:5}")
    private int activeTransaction;

    @Value("${app.config.passive_transaction:2}")
    private int passiveTransaction;

    @Value("${app.config.dicebear_url}")
    private String dicebearUrl;

    @Value("${app.config.avatar_file_size}")
    private int avatarFileSize;

    @Value("${app.config.avatar_bucket_name}")
    private String avatarBucketName;

    @Value("${app.config.avatar_large_file_size}")
    private int avatarLargeFileSize;

    @Value("${app.config.avatar_small_file_size}")
    private int avatarSmallFileSize;
}
