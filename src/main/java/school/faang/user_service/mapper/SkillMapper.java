package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toEntity(SkillDto skill);

    SkillDto toDto(Skill skill);

    Skill toSkillEntityFromCreateDto(CreateSkillDto skillDto);

    SkillCandidateDto toCandidateDto(Skill skill);

    @Named("mapSkillsToIds")
    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? List.of() : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mapIdsToSkills")
    default List<Skill> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) {
            return List.of();
        }
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
                .collect(Collectors.toList());
    }
}