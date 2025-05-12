package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
public class RecommendationDto {
    private Long id;
    private Long author;
    private Long receiver;
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
