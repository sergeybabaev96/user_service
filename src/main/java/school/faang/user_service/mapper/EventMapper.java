package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy =  ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "relatedSkills", ignore = true)
    Event eventDtoToEvent(EventDto eventDto);
    @Mapping(source="owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", expression = "java(mapSkillsId(event.getRelatedSkills()))")
    EventDto eventToEventDto(Event event);

    default List<Long> mapSkillsId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
