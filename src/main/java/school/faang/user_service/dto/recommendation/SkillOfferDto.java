package school.faang.user_service.dto.recommendation;

Рimport lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}
