package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;

@Data

public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
