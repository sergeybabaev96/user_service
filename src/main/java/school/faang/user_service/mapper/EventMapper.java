package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    Event toEntity(EventResponse eventResponse);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(target = "relatedSkills", expression = "java(idsToSkills(eventDto.relatedSkills()))")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", expression = "java(skillsToSkillIds(event.getRelatedSkills()))")
    EventDto toDto(Event event);

    EventResponse toEventResponse(Event event);

    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills != null ? skills.stream().map(Skill::getId).collect(Collectors.toList()) : null;
    }

    default List<Skill> idsToSkills(List<Long> ids) {
        return ids != null ? ids.stream().map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
        }).collect(Collectors.toList()) : null;
    }
}