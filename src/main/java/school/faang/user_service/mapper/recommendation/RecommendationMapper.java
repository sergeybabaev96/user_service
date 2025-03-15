package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "skillOffers", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOfferDtos",
            expression = "java(SkillOfferMapper.toSkillOfferDtos(recommendation.getSkillOffers()))")
    RecommendationDto toDto(Recommendation recommendation);
}
