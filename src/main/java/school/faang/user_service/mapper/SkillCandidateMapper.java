package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING) 
public interface SkillCandidateMapper {
    SkillCandidateDto skillMapToSkillCandidateDto(SkillDto skill, Long offersAmount);
}
