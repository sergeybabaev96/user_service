package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffersId")
    RecommendationDto toDto(Recommendation recommendation);
    List<SkillOfferDto> mapSkillOffers(List<SkillOffer> skillOffers);
    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto skillOfferToSkillOfferDto(SkillOffer skillOffer);

    List<RecommendationDto> toListRecommendationDtos(List<Recommendation> recommendations);
}
