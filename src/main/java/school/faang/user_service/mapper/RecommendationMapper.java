package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Recommendation toEntity (RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationDto toDto (Recommendation recommendation);
}
