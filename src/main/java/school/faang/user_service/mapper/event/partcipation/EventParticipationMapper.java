package school.faang.user_service.mapper.event.partcipation;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.participant.EventParticipationDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventParticipationMapper {
    Event toEntity(EventParticipationDto eventParticipationDto);

    EventParticipationDto toDto(Event event);

    List<Event> toDtoList(List<EventParticipationDto> events);
}