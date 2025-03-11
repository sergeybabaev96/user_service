package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapperDecorator.class)
public interface EventMapper {

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapSkillsToIds")
    EventDto toDto(Event event);

    @Mapping(source = "relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    void updateEventFormDto(EventDto eventDto, @MappingTarget Event event);

    @Named("mapSkillsToIds")
    static List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
