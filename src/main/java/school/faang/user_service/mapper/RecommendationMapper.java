package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "receiverId", ignore = true)
    @Mapping(target = "skillOffersId", ignore = true)
    RecommendationDto toDto(Recommendation recommendation);
    List<RecommendationDto> toListRecommendationDtos(List<Recommendation> recommendations);


}
