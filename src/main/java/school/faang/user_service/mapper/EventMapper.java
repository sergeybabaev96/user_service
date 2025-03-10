package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventMapper {
    private SkillMapper skillMapper;

    @Autowired
    public void setSkillMapper(SkillMapper skillMapper) {
        this.skillMapper = skillMapper;
    }

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    public abstract Event toEventEntity(EventRequestDto dto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillsDto", qualifiedByName = "mapToRelatedSkillsDto")
    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    public abstract EventDto toEventDto(Event entity);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    public abstract void update(EventRequestDto dto, @MappingTarget Event entity);

    @Named("mapToRelatedSkillsDto")
    protected List<ResponseSkillDto> mapToRelatedSkillsDto(List<Skill> relatedSkills) {
        return relatedSkills == null ? null : relatedSkills.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }
}
