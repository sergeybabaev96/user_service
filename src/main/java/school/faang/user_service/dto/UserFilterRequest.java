package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserFilterRequest(
        @NotNull
        @NotBlank
        String namePattern,
        @NotNull
        @NotBlank
        String phonePattern,
        @NotNull
        @Min(1)
        Integer experienceMin,
        @NotNull
        @Min(1)
        Integer experienceMax
) {
}
