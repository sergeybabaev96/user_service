package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    Long id;
    Long authorId;
    Long receiverId;
    String content;
    List<SkillOfferDto> skillOffers;
    LocalDateTime createdAt;
}
