package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectionDto(@NotBlank String reason) {
}
