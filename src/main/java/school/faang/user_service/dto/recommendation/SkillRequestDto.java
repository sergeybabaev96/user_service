package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SkillRequestDto(
        @NotNull(message = "Id can't be null")
        @Positive(message = "Id can't be negative")
        long skillId
) {
}
