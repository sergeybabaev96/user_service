package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    Recommendation toEntity(RecommendationDto recommendationDto);
    RecommendationDto toDto(Recommendation recommendation);
}
