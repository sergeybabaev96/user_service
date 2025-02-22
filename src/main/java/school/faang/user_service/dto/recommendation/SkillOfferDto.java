package school.faang.user_service.dto.recommendation;

import lombok.NonNull;

public record SkillOfferDto(
        Long id,
        @NonNull
        Long skillId) {
}

