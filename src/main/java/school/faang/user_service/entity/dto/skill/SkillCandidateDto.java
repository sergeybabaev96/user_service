package school.faang.user_service.entity.dto.skill;

import lombok.Data;

@Data
public class SkillCandidateDto {
    private SkillDto skill;
    private Long offersAmount;
}
