package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper
public interface RecommendationRequestMapper {

    RecommendationRequest toRecommendationRequest(RecommendationRequestDto recommendationRequestDto);

    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);
}
