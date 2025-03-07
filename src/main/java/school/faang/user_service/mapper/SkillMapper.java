package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);
    Skill toEntity(SkillDto skillDto);
}
