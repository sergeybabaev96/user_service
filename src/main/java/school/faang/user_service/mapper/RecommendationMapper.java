package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.recommendation.RecommendationDto;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", ignore = true)
    RecommendationDto toDto(Recommendation entity);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skillOffers", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recommendation toEntity(RecommendationDto dto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", ignore = true)
    void update(@MappingTarget RecommendationDto dto, Recommendation entity);
}
