package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import java.time.LocalDateTime;

@Component
public class RecommendationEventMapper {
    public RecommendationEvent mapToRecommendationEvent(RecommendationDto recommendation) {
        return RecommendationEvent.builder()
                .recommendationId(recommendation.getId())
                .authorId(recommendation.getAuthorId())
                .receiverId(recommendation.getReceiverId())
                .createdAt(recommendation.getCreatedAt() != null ? recommendation.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}

