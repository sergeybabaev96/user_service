package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SkillOfferMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.title", target = "title")
    @Mapping(source = "recommendation.receiver.id", target = "receiverUserId")
    @Mapping(source = "recommendation.author.id", target = "requesterUserId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "title", target = "skill.title")
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);
}
