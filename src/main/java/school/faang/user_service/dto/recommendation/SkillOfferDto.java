package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkillOfferDto {
    private Long id;
    private String title;
    private Long skillId;
    private Long receiverUserId;
    private Long requesterUserId;
}
