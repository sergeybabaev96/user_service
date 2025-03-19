package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;

@Data
public class SkillOfferDto {
    private final long id;
    private final Skill skill;
    private final Recommendation recommendation;
}
