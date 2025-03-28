package school.faang.user_service.service.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParserErrorMessages {

    FAILED_CSV("Failed to process CSV file");

    private final String message;
}
