package school.faang.user_service.dto.avatar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvatarType {
    SVG(".svg", "image/svg+xml", 200, 200),
    PNG(".png", "image/png", 400, 400),
    JPEG(".jpg", "image/jpeg", 400, 400);

    private final String extension; //рассширение для файла
    private final String contentType; // нужен для S3 чтоб  браузер не тупил при открытие
    private final int width; //ну и просто размер авотара :)
    private final int height;
}
