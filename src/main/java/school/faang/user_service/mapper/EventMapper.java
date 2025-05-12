package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto dto);

    @Mapping(target = "relatedSkills", expression = "java(mapSkillsToIds(entity.getRelatedSkills()))")
    EventDto toDto(Event entity);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
