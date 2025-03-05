package school.faang.user_service.repository.recommendation;

import lombok.Data;

@Data
public class SkillOfferDto {
    private long id;
    private long skillId;
    private long recommendationId;
}
