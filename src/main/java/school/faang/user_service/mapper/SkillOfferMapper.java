package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    SkillOfferDto toDto(SkillOffer skillOffer);

    List<SkillOfferDto> skillOffersToSkillOfferDtos(List<SkillOffer> skillOffers);
}
