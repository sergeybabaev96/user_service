package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    Skill toSkillEntity(CreateSkillDto skillDto);

    ResponseSkillDto toSkillDto(Skill skill);
}
