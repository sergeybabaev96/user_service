package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecommendationRequestDto(
        @Positive
        Long requesterId,
        @Positive
        Long receiverId,
        @Size(max = 4096)
        @NotBlank
        String message,
        List<Long> skillsIds) {
}
