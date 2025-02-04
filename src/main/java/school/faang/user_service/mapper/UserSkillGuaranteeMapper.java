package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring")
public interface UserSkillGuaranteeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", source = "receiverId")
    @Mapping(target = "guarantor.id", source = "authorId")
    @Mapping(target = "skill", source = "skill")
    UserSkillGuarantee toEntity(Long receiverId, Long authorId, Skill skill);
}