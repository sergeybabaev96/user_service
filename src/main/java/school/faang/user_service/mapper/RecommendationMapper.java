package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapIdToUser")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapIdToUser")
    @Mapping(target = "skillOffers", source = "skillOffersDto")
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffersDto", source = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);

    default List<SkillOffer> mapSkillOfferDtosToSkillOffers(List<SkillOfferDto> skillOfferDtos) {
        if (skillOfferDtos == null) {
            return null;
        }
        return skillOfferDtos.stream()
                .map(dto -> {
                    SkillOffer skillOffer = new SkillOffer();
                    skillOffer.setId(dto.getId());
                    skillOffer.getSkill().setId(dto.getSkillDto().getId());
                    return skillOffer;
                })
                .toList();
    }

    default List<SkillOfferDto> mapSkillOffersToSkillOfferDtos(List<SkillOffer> skillOffers) {
        if (skillOffers == null) {
            return null;
        }
        return skillOffers.stream()
                .map(skillOffer -> {
                    SkillOfferDto dto = new SkillOfferDto();
                    dto.setId(skillOffer.getId());
                    dto.getSkillDto().setId(skillOffer.getSkill().getId());
                    return dto;
                })
                .toList();
    }

    @Named("mapIdToUser")
    default User mapAuthorIdToUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
