package school.faang.user_service.dto.recommendation;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecommendationDto {
    Long id;
    Long authorId;
    Long receiverId;
    String content;
    List<SkillOfferDto> skillOffers;
    LocalDateTime createdAt;
}
