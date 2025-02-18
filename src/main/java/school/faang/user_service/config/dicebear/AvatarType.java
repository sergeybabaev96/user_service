package school.faang.user_service.config.dicebear;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvatarType {
    SVG(".svg", "image/svg+xml", 200, 200),
    PNG(".png", "image/png", 400, 400),
    JPEG(".jpg", "image/jpeg", 400, 400);

    private final String extension;
    private final String contentType;
    private final int width;
    private final int height;
}
