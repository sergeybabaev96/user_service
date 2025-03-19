package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillDto toDto(Skill skill);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "guarantees", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(SkillDto skillDto);

    @Mapping(target = "offerAmount", ignore = true)
    SkillCandidateDto toSkillCandidateDto(Skill skill);
}
