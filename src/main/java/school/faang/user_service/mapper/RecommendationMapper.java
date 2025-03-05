package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.recommendation.RecommendationDto;

@Mapper
public interface RecommendationMapper {
    public RecommendationDto toDto(Recommendation entity);
    public Recommendation toEntity(RecommendationDto dto);
}
