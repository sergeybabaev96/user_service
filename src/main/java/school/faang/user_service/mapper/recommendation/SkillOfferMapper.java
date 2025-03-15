package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);
    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    static List<SkillOfferDto> toSkillOfferDtos (List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map((new SkillOfferMapperImpl())::toDto)
                .toList();
    }
}