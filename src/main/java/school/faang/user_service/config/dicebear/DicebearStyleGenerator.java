package school.faang.user_service.config.dicebear;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.avatar.DicebearStyle;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class DicebearStyleGenerator {
    private final SecureRandom random;
    public String getRandomStyleString() {
        DicebearStyle[] styles = DicebearStyle.values();
        return styles[random.nextInt(styles.length)].getStyleName(); // например: "pixel-art"
    }
}
