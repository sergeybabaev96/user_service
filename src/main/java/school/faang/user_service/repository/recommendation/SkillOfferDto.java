package school.faang.user_service.repository.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SkillOfferDto {
    private long Id;
    private long skillId;
    private long recommendationId;
}
