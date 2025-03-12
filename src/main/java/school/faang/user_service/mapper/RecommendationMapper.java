package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", uses = {SkillOfferMapper.class})
public interface RecommendationMapper {
    //This mapper for work with ViewDto
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(source = "skillOffers", target = "skillOffers")
    Recommendation viewDtoToEntity(RecommendationViewDto recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    RecommendationViewDto toViewDto(Recommendation recommendation);

    //This mapper for work with CreateDto
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(source = "skillOffers", target = "skillOffers")
    Recommendation createDtoToEntity(RecommendationCreateDto recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    RecommendationCreateDto toCreateDto(Recommendation recommendation);
}
