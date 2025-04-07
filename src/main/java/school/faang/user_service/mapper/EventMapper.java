package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "type", source = "eventType")
    Event toEntity(EventCreateDto eventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "relatedSkillsId", source = "relatedSkills", qualifiedByName = "skillsToIds")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventViewDto toDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EventCreateDto eventDto, @MappingTarget Event event);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
