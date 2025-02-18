package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "relatedSkillIds", source = "relatedSkills", qualifiedByName = "mapSkillsToSkillIds")
    EventDto toDto(Event event);

    List<EventDto> toDtoList(List<Event> events);

    @Named("mapSkillsToSkillIds")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId).toList();
    }
}
