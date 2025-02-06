package school.faang.user_service.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Event toEntityEvent(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "toIdFromSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Named("toIdFromSkills")
    default List<Long> toIdFromSkills(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    void updateEntityFromDto(@MappingTarget Event entity,EventUpdateDto dto);

}