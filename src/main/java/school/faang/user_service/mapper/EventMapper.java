package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapSkillsToIds")
    public abstract EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwnerById")
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapIdsToSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Event toEntity(EventDto eventDto, @Context SkillRepository skillRepository);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwnerById")
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "mapIdsToSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEventFormDto(EventDto eventDto, @MappingTarget Event event, @Context SkillRepository skillRepository);

    @Named("mapSkillsToIds")
    static List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mapIdsToSkills")
    List<Skill> mapIdsToSkills(List<Long> skillIds, @Context SkillRepository skillRepository) {
        return skillIds == null ? null : skillRepository.findAllById(skillIds);
    }

    @Named("mapOwnerById")
    static User mapOwnerById(Long ownerId) {
        return ownerId == null ? null : User.builder().id(ownerId).build();
    }
}
