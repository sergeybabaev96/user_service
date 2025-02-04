package school.faang.user_service.entity.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {
    en("en"),
    ru("ru"),
    fr("fr"),
    de("de");

    private final String tag;
}
