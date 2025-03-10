package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus")
    Event toEntity(EventDto eventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "relatedSkillsId", source = "relatedSkills", qualifiedByName = "skillsToIds")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventDto toDto(Event event);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
