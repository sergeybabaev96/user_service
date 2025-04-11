package school.faang.user_service.dto.avatar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DicebearStyle {
    BOTTT("bottts"),
    ADVENTURER("adventurer"),
    PIXEL_ART("pixel-art"),
    THUMBS("thumbs");

    private final String styleName;
}

