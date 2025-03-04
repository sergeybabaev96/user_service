package school.faang.user_service.repository.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SkillOfferDto {
    private long skillId;
    private long recommendationId;
}
