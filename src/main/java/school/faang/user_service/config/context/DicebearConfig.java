package school.faang.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "dicebear")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class DicebearConfig {
    private final List<String> styleName;
    private final List<String> seed;
}
