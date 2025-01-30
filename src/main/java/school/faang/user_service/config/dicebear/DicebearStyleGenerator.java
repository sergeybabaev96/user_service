package school.faang.user_service.config.dicebear;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class DicebearStyleGenerator {
    private final SecureRandom secureRandom;

    public String getRandomStyleString() {
        DicebearStyle[] styles = DicebearStyle.values();
        return styles[secureRandom.nextInt(styles.length)]
                .toString()
                .toLowerCase();
    }
}