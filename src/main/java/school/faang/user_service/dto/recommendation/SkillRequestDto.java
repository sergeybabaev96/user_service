package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillRequestDto {
    private Long id;
    private Long recommendationRequestId;
    private Long skillId;
    private String skillTitle;
}
