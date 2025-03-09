package school.faang.user_service.dto.recommendation;

import org.springframework.lang.Nullable;

public record SkillRequestDto(
        long skillId,
        @Nullable Long requestId) {
}
