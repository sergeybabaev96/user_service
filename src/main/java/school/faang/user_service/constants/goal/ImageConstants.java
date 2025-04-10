package school.faang.user_service.constants.goal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageConstants {

    public static final String IMAGE_FORMAT_REGEX = "image/(jpeg|png|webp)";
    public static final String DEFAULT_FORMAT = "jpg";
    public static final Pattern FORMAT_EXTRACTOR = Pattern.compile(IMAGE_FORMAT_REGEX, Pattern.CASE_INSENSITIVE);
}
