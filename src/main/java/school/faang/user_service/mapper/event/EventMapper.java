package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

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

    List<EventDTO> eventsToEventDTOs(List<Event> events);

    List<Event> eventDTOsToEvents(List<EventDTO> eventDTOs);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    default List<Skill> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) return null;
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();  // Создаем новый объект Skill
                    // Присваиваем ID вручную для гарантии что
                    // @GeneratedValue(strategy = GenerationType.IDENTITY) отработает верно
                    skill.setId(id);
                    return skill;
                })
                .collect(Collectors.toList());
    }
}
