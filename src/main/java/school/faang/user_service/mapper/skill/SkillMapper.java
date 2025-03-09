package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skill);
}
