package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(target = "skill", source = "skillId")
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toSkillOffer(SkillOfferDto skillOfferDto);

    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtos);

    @Mapping(target = "skillId", source = "skill.id")
    SkillOfferDto toSkillOfferDto(SkillOffer skillOffer);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);

    default Skill mapIdToSkill(Long id) {
        if (id == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }
}
