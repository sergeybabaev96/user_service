package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "skillOffers", source = "skillOfferIds")
    Recommendation toEntity(RecommendationDto recommendation);

    @Mapping(target = "skillOfferIds", source = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);

    default List<SkillOffer> mapSkillOfferIdsToSkillOffers(List<Long> skillOffersIds) {
        return skillOffersIds.stream()
                .map(id -> {
                    SkillOffer skillOffer = new SkillOffer();
                    skillOffer.setId(id);
                    return skillOffer;
                })
                .collect(Collectors.toList());
    }

    default List<Long> mapSkillOffersToSkillOfferIds(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOffer::getId)
                .collect(Collectors.toList());
    }
}
