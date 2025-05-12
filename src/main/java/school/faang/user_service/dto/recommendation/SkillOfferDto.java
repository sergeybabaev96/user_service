package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SkillOfferDto {
    private Long id;
    private Long skill;
    private Long recommendation;
}
