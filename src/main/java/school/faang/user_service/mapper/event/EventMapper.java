package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventDTO eventToEventDTO(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event eventDTOToEvent(EventDTO eventDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    void updateEventFromDTO(EventDTO source, @MappingTarget Event target);

    List<EventDTO> eventsToEventDTOs(List<Event> events);

    List<Event> eventDTOsToEvents(List<EventDTO> eventDTOs);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .collect(toList());
    }

    default List<Skill> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) return null;
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
                .collect(toList());
    }
}
